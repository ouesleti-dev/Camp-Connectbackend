package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Coupon;
import org.example.campconnect.Repository.CouponRepository;
import org.example.campconnect.dto.CouponRequest;
import org.example.campconnect.dto.CouponResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService {

    private final CouponRepository couponRepository;

    @Override
    public CouponResponseDTO createCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new RuntimeException("Coupon code already exists");
        }
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .discountPercentage(request.getDiscountPercentage())
                .expirationDate(request.getExpirationDate())
                .maxUses(request.getMaxUses())
                .build();
        return toDTO(couponRepository.save(coupon));
    }

    @Override
    public CouponResponseDTO validateCoupon(String code) {
        return couponRepository.findByCode(code.toUpperCase())
                .map(c -> {
                    if (!c.getActive()) {
                        return invalid(c, "Ce code promo est désactivé.");
                    }
                    if (c.getExpirationDate().before(new Date())) {
                        return invalid(c, "Ce code promo a expiré.");
                    }
                    if (c.getCurrentUses() >= c.getMaxUses()) {
                        return invalid(c, "Ce code promo a atteint son nombre d'utilisations maximum.");
                    }
                    return CouponResponseDTO.builder()
                            .idCoupon(c.getIdCoupon())
                            .code(c.getCode())
                            .discountPercentage(c.getDiscountPercentage())
                            .expirationDate(c.getExpirationDate())
                            .maxUses(c.getMaxUses())
                            .currentUses(c.getCurrentUses())
                            .active(c.getActive())
                            .valid(true)
                            .message("Code valide ! -" + c.getDiscountPercentage().intValue() + "%")
                            .build();
                })
                .orElse(CouponResponseDTO.builder()
                        .valid(false)
                        .message("Code promo introuvable.")
                        .build());
    }

    @Override
    public List<CouponResponseDTO> getAllCoupons() {
        return couponRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public CouponResponseDTO toggleActive(Long id) {
        Coupon c = couponRepository.findById(id).orElseThrow(() -> new RuntimeException("Coupon not found"));
        c.setActive(!c.getActive());
        return toDTO(couponRepository.save(c));
    }

    private CouponResponseDTO toDTO(Coupon c) {
        return CouponResponseDTO.builder()
                .idCoupon(c.getIdCoupon())
                .code(c.getCode())
                .discountPercentage(c.getDiscountPercentage())
                .expirationDate(c.getExpirationDate())
                .maxUses(c.getMaxUses())
                .currentUses(c.getCurrentUses())
                .active(c.getActive())
                .valid(true)
                .build();
    }

    private CouponResponseDTO invalid(Coupon c, String message) {
        return CouponResponseDTO.builder()
                .code(c.getCode())
                .valid(false)
                .message(message)
                .build();
    }
}
