package com.ecommerce.controller;

import com.ecommerce.dto.request.CreateCouponRequest;
import com.ecommerce.entity.Coupon;
import com.ecommerce.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupons", description = "Coupon creation and management")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @Operation(summary = "Create a new coupon")
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        log.info("Creating coupon with code={}", request.getCode());
        Coupon coupon = couponService.createCoupon(request);
        log.info("Coupon created successfully with id={}, code={}", coupon.getId(), coupon.getCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
    }

    @GetMapping
    @Operation(summary = "Get all coupons")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        log.info("Fetching all coupons");
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @DeleteMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a coupon by id")
    public ResponseEntity<String> deactivateCoupon(@PathVariable Long id) {
        log.info("Deactivating coupon with id={}", id);
        couponService.deactivateCoupon(id);
        log.info("Coupon with id={} deactivated successfully", id);
        return ResponseEntity.ok("Coupon deactivated successfully.");
    }
}