package com.ecommerce.service;

import com.ecommerce.dto.request.AddToCartRequest;
import com.ecommerce.dto.request.UpdateCartRequest;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ExceptionConstants;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+request.getProductId()));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new ResourceNotFoundException(
                    ExceptionConstants.INSUFFICIENT_STOCK + product.getName() + ". Requested: " + request.getQuantity() + ", Available: " + product.getStockQuantity());
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQty = cartItem.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQty) {
                throw new ResourceNotFoundException(
                        ExceptionConstants.INSUFFICIENT_STOCK + product.getName() + ". Requested: " + newQty + ", Available: " + product.getStockQuantity());
            }
            cartItem.setQuantity(newQty);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem(cart, product, request.getQuantity());
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND + userId));
        return new CartResponse(updatedCart);
    }

    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem does not belong to user's cart");
        }

        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < request.getQuantity()) {
                 throw new ResourceNotFoundException(
                         ExceptionConstants.INSUFFICIENT_STOCK + product.getName() + ". Requested: " + request.getQuantity() + ", Available: " + product.getStockQuantity());
             }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+cartItemId));
        return new CartResponse(updatedCart);
    }

    @Transactional
    public CartResponse removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem does not belong to user's cart");
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        return new CartResponse(updatedCart);
    }

    public CartResponse getCart(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() ->new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+userId));

        return new CartResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND+userId));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}