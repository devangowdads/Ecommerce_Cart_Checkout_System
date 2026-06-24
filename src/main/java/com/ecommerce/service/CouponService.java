package com.ecommerce.service;

import com.ecommerce.dto.request.CreateCouponRequest;
import com.ecommerce.entity.Coupon;
import com.ecommerce.exception.ExceptionConstants;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public Coupon createCoupon(CreateCouponRequest request) {
        if (couponRepository.findByCodeAndActiveTrue(request.getCode()).isPresent()) {
            throw new ResourceNotFoundException(ExceptionConstants.DUPLICATE_RESOURCE + request.getCode() + "' already exists.");
        }
        Coupon coupon = new Coupon(
                request.getCode().toUpperCase(),
                request.getDiscountType(),
                request.getDiscountValue(),
                request.getExpiryDate()
        );
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Transactional
    public void deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.RESOURCE_NOT_FOUND +id));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }
}