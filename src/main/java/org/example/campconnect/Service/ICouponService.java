package org.example.campconnect.Service;

import org.example.campconnect.dto.CouponRequest;
import org.example.campconnect.dto.CouponResponseDTO;

import java.util.List;

public interface ICouponService {
    CouponResponseDTO createCoupon(CouponRequest request);
    CouponResponseDTO validateCoupon(String code);
    List<CouponResponseDTO> getAllCoupons();
    void deleteCoupon(Long id);
    CouponResponseDTO toggleActive(Long id);
}
