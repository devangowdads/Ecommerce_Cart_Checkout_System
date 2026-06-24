package com.ecommerce.dto.response;

import com.ecommerce.entity.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.unitPrice = cartItem.getProduct().getPrice();
        this.quantity = cartItem.getQuantity();
        this.subtotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        this.createdAt = cartItem.getCreatedAt();
        this.updatedAt = cartItem.getUpdatedAt();
    }
}