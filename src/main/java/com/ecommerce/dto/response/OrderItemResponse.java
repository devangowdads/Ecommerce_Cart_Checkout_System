package com.ecommerce.dto.response;

import com.ecommerce.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderItemResponse(OrderItem orderItem) {
        this.orderItemId = orderItem.getId();
        this.productId = orderItem.getProduct().getId();
        this.productName = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
        this.priceAtOrder = orderItem.getPrice();
        this.subtotal = orderItem.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        this.createdAt = orderItem.getCreatedAt();
        this.updatedAt = orderItem.getUpdatedAt();
    }
}