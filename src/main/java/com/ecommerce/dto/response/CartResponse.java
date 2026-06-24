package com.ecommerce.dto.response;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private String userName;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartResponse(Cart cart) {
        this.cartId = cart.getId();
        this.userId = cart.getUser().getId();
        this.userName = cart.getUser().getName();

        this.items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (cart.getCartItems() != null) {
            for (CartItem cartItem : cart.getCartItems()) {
                CartItemResponse itemResponse = new CartItemResponse(cartItem);
                this.items.add(itemResponse);
                total = total.add(itemResponse.getSubtotal());
            }
        }

        this.totalItems = this.items.size();
        this.totalAmount = total;
        this.createdAt = cart.getCreatedAt();
        this.updatedAt = cart.getUpdatedAt();
    }
}