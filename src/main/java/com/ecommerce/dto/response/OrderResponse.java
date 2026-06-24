package com.ecommerce.dto.response;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.enums.OrderStatus;
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
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private String userName;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String couponCode;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUser().getId();
        this.userName = order.getUser().getName();

        this.items =new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                this.items.add(new OrderItemResponse(orderItem));
            }
        }

        this.totalAmount = order.getTotalAmount();
        this.discountAmount = order.getDiscountAmount();
        this.finalAmount = order.getFinalAmount();
        this.couponCode = order.getCouponCode();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}