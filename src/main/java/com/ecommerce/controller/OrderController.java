package com.ecommerce.controller;

import com.ecommerce.dto.request.CheckoutRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.enums.OrderStatus;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Checkout, order lookup, and order history")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{userId}/checkout")
    @Operation(summary = "Checkout the cart and create an order")
    public ResponseEntity<OrderResponse> checkout(@PathVariable Long userId, @RequestBody(required = false) CheckoutRequest request) {
        if (request == null) {
            request = new CheckoutRequest();
        }
        log.info("Checkout initiated for userId={}", userId);
        OrderResponse response = orderService.checkout(userId, request);
        log.info("Checkout completed for userId={}, orderId={}, status={}",
                userId, response.getOrderId(), response.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by order id")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        log.debug("Fetching order with id={}", orderId);
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get paginated order history for a user")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUser(
            @PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        log.debug("Fetching order history for userId={}, page={}, size={}", userId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getOrdersByUser(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}/status")
    @Operation(summary = "Get paginated order history for a user filtered by status")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserAndStatus(
            @PathVariable Long userId, @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        log.debug("Fetching order history for userId={}, status={}, page={}, size={}", userId, status, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getOrdersByUserAndStatus(userId, status, pageable);
        return ResponseEntity.ok(orders);
    }
}