package com.ecommerce.controller;

import com.ecommerce.dto.request.AddToCartRequest;
import com.ecommerce.dto.request.UpdateCartRequest;
import com.ecommerce.dto.response.CartResponse;
import com.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Cart item operations")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add")
    @Operation(summary = "Add a product to the cart")
    public ResponseEntity<CartResponse> addToCart(@PathVariable Long userId,@Valid @RequestBody AddToCartRequest request) {
        log.info("Adding productId={} qty={} to cart for userId={}", request.getProductId(), request.getQuantity(), userId);
        CartResponse response = cartService.addToCart(userId, request);
        log.info("Cart updated successfully for userId={}", userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/item/{cartItemId}")
    @Operation(summary = "Update quantity of a cart item")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long userId,@PathVariable Long cartItemId,@Valid @RequestBody UpdateCartRequest request) {
        log.info("Updating cartItemId={} for userId={} to qty={}", cartItemId, userId, request.getQuantity());
        CartResponse response = cartService.updateCartItem(userId, cartItemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/item/{cartItemId}")
    @Operation(summary = "Remove an item from the cart")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long userId,@PathVariable Long cartItemId) {
        log.info("Removing cartItemId={} for userId={}", cartItemId, userId);
        CartResponse response = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "View the current cart")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/clear")
    @Operation(summary = "Clear the entire cart")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        log.info("Clearing cart for userId={}", userId);
        cartService.clearCart(userId);
        log.info("Cart cleared for userId={}", userId);
        return ResponseEntity.ok("Cart cleared successfully.");
    }
}