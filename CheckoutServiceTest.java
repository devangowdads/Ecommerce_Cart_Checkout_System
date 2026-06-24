package com.ecommerce.service;

import com.ecommerce.dto.request.CheckoutRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.entity.*;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.exception.CartEmptyException;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.InvalidCouponException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User("John Doe", "john@example.com");
        user.setId(1L);

        product = new Product("Laptop", new BigDecimal("50000"), 10, "Electronics laptop", "Electronics");
        product.setId(1L);

        cart = new Cart(user);
        cart.setId(1L);

        cartItem = new CartItem(cart, product, 2);
        cartItem.setId(1L);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
    }

    @Test
    @DisplayName("Checkout fails when user not found")
    void checkout_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CheckoutRequest request = new CheckoutRequest();
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.checkout(99L, request));
    }

    @Test
    @DisplayName("Checkout fails when cart not found")
    void checkout_CartNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        CheckoutRequest request = new CheckoutRequest();
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.checkout(1L, request));
    }

    @Test
    @DisplayName("Checkout fails when cart is empty")
    void checkout_CartEmpty_ThrowsException() {
        cart.setCartItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CheckoutRequest request = new CheckoutRequest();
        assertThrows(CartEmptyException.class,
                () -> orderService.checkout(1L, request));
    }

    @Test
    @DisplayName("Checkout fails when stock is insufficient")
    void checkout_InsufficientStock_ThrowsException() {
        product.setStockQuantity(1); // only 1 in stock but cart has 2

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CheckoutRequest request = new CheckoutRequest();
        assertThrows(InsufficientStockException.class,
                () -> orderService.checkout(1L, request));
    }

    @Test
    @DisplayName("Checkout fails with invalid coupon code")
    void checkout_InvalidCoupon_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(couponRepository.findByCodeAndActiveTrue("BADCODE")).thenReturn(Optional.empty());

        CheckoutRequest request = new CheckoutRequest();
        request.setCouponCode("BADCODE");

        assertThrows(InvalidCouponException.class,
                () -> orderService.checkout(1L, request));
    }

    @Test
    @DisplayName("Checkout fails with expired coupon")
    void checkout_ExpiredCoupon_ThrowsException() {
        Coupon expiredCoupon = new Coupon("EXPIRED10", DiscountType.PERCENTAGE,
                new BigDecimal("10"), LocalDate.now().minusDays(1));
        expiredCoupon.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(couponRepository.findByCodeAndActiveTrue("EXPIRED10")).thenReturn(Optional.of(expiredCoupon));

        CheckoutRequest request = new CheckoutRequest();
        request.setCouponCode("EXPIRED10");

        assertThrows(InvalidCouponException.class,
                () -> orderService.checkout(1L, request));
    }

    @Test
    @DisplayName("Total amount calculated correctly without coupon")
    void checkout_TotalAmountCalculation_IsCorrect() {
        // product price = 50000, qty = 2, total = 100000
        BigDecimal expectedTotal = new BigDecimal("100000");

        Order savedOrder = new Order(user, expectedTotal, BigDecimal.ZERO, expectedTotal, OrderStatus.SUCCESS, null);
        savedOrder.setId(100L);
        savedOrder.setOrderItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(1);

        CheckoutRequest request = new CheckoutRequest();
        OrderResponse response = orderService.checkout(1L, request);

        assertEquals(expectedTotal, response.getTotalAmount());
    }

    @Test
    @DisplayName("Percentage coupon discount applied correctly")
    void checkout_PercentageCoupon_DiscountAppliedCorrectly() {
        // total = 100000, 10% off = 10000 discount, final = 90000
        Coupon coupon = new Coupon("SAVE10", DiscountType.PERCENTAGE,
                new BigDecimal("10"), LocalDate.now().plusDays(30));
        coupon.setId(1L);

        BigDecimal total = new BigDecimal("100000");
        BigDecimal discount = new BigDecimal("10000.00");
        BigDecimal finalAmt = new BigDecimal("90000.00");

        Order savedOrder = new Order(user, total, discount, finalAmt, OrderStatus.SUCCESS, "SAVE10");
        savedOrder.setId(101L);
        savedOrder.setOrderItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(couponRepository.findByCodeAndActiveTrue("SAVE10")).thenReturn(Optional.of(coupon));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(1);

        CheckoutRequest request = new CheckoutRequest();
        request.setCouponCode("SAVE10");

        OrderResponse response = orderService.checkout(1L, request);

        assertEquals("SAVE10", response.getCouponCode());
        assertEquals(discount, response.getDiscountAmount());
        assertEquals(finalAmt, response.getFinalAmount());
    }

    @Test
    @DisplayName("Flat coupon discount applied correctly")
    void checkout_FlatCoupon_DiscountAppliedCorrectly() {
        // total = 100000, flat 500 off, final = 99500
        Coupon coupon = new Coupon("FLAT500", DiscountType.FLAT,
                new BigDecimal("500"), LocalDate.now().plusDays(30));
        coupon.setId(2L);

        BigDecimal total = new BigDecimal("100000");
        BigDecimal discount = new BigDecimal("500");
        BigDecimal finalAmt = new BigDecimal("99500");

        Order savedOrder = new Order(user, total, discount, finalAmt, OrderStatus.SUCCESS, "FLAT500");
        savedOrder.setId(102L);
        savedOrder.setOrderItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(couponRepository.findByCodeAndActiveTrue("FLAT500")).thenReturn(Optional.of(coupon));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(1);

        CheckoutRequest request = new CheckoutRequest();
        request.setCouponCode("FLAT500");

        OrderResponse response = orderService.checkout(1L, request);

        assertEquals(discount, response.getDiscountAmount());
        assertEquals(finalAmt, response.getFinalAmount());
    }

    @Test
    @DisplayName("Order history returns paginated results")
    void getOrdersByUser_ReturnsPagedOrders() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(eq(1L), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        var result = orderService.getOrdersByUser(1L,
                org.springframework.data.domain.PageRequest.of(0, 10));

        assertNotNull(result);
        verify(orderRepository, times(1)).findByUserId(eq(1L), any());
    }
}
