package com.ecommerce.service;

import com.ecommerce.dto.request.CheckoutRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.entity.*;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.exception.*;
import com.ecommerce.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository,
                        CouponRepository couponRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->  new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND + userId));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw  new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND);
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                    throw new ResourceNotFoundException(
                            ExceptionConstants.INSUFFICIENT_STOCK + product.getName() + ". Requested: " + cartItem.getQuantity() + ", Available: " + product.getStockQuantity());
                }
               
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCouponCode = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(request.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ExceptionConstants.INVALID_COUPON+
                            "Coupon '" + request.getCouponCode() + "' is invalid or inactive."));

            if (coupon.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                throw new ResourceNotFoundException(
                        ExceptionConstants.INVALID_COUPON+"Coupon '" + request.getCouponCode() + "' has expired.");
            }

            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                discountAmount = totalAmount
                        .multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                // FLAT discount
                discountAmount = coupon.getDiscountValue().min(totalAmount);
            }
            appliedCouponCode = coupon.getCode();
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Order order = new Order(user, totalAmount, discountAmount, finalAmount, OrderStatus.PENDING, appliedCouponCode);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
            );
            orderItems.add(orderItem);
        }
        savedOrder.setOrderItems(orderItems);

        boolean paymentSuccess = simulatePayment();

        if (paymentSuccess) {
            for (CartItem cartItem : cartItems) {
                int updated = productRepository.decreaseStock(
                        cartItem.getProduct().getId(), cartItem.getQuantity());
                if (updated == 0) {
                            
                        throw new ResourceNotFoundException(
                                ExceptionConstants.INSUFFICIENT_STOCK +  cartItem.getProduct().getName() + ". Requested: " +  cartItem.getQuantity() + ", Available: " +  cartItem.getProduct().getStockQuantity());
                    }
                
            }

            savedOrder.setStatus(OrderStatus.SUCCESS);

            cart.getCartItems().clear();
            cartRepository.save(cart);

        } else {
            savedOrder.setStatus(OrderStatus.FAILED);
        }

        Order finalOrder = orderRepository.save(savedOrder);
        return new OrderResponse(finalOrder);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+orderId));
        return new OrderResponse(order);
    }

    public Page<OrderResponse> getOrdersByUser(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+ userId));
        return orderRepository.findByUserId(userId, pageable).map(OrderResponse::new);
    }

    public Page<OrderResponse> getOrdersByUserAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+ userId));
        return orderRepository.findByUserIdAndStatus(userId, status, pageable).map(OrderResponse::new);
    }

    private boolean simulatePayment() {
        return new Random().nextInt(10) < 7;
    }
}