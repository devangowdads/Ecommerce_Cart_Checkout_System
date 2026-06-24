package com.ecommerce.exception;


public class ExceptionConstants {

    private ExceptionConstants() {
    }

    public static final String RESOURCE_NOT_FOUND = "%s not found with %s: '%s'";

    public static final String DUPLICATE_RESOURCE = "%s already exists with %s: '%s'";

    public static final String CART_EMPTY = "Cart is empty for userId: %d. Cannot proceed with checkout.";

    public static final String INSUFFICIENT_STOCK = "Insufficient stock for product ";

    public static final String INVALID_COUPON = "Invalid or expired coupon code: '%s'";
    public static final String COUPON_EXPIRED = "Coupon '%s' expired on %s";
}