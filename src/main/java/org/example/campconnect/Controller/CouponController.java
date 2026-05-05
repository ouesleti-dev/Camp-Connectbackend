package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ICouponService;
import org.example.campconnect.dto.CouponRequest;
import org.example.campconnect.dto.CouponResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final ICouponService couponService;

    // Admin: créer un coupon
    @PostMapping
    public ResponseEntity<CouponResponseDTO> create(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }

    // Client: valider un code
    @GetMapping("/validate/{code}")
    public ResponseEntity<CouponResponseDTO> validate(@PathVariable String code) {
        return ResponseEntity.ok(couponService.validateCoupon(code));
    }

    // Admin: liste tous les coupons
    @GetMapping
    public ResponseEntity<List<CouponResponseDTO>> getAll() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // Admin: supprimer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    // Admin: activer/désactiver
    @PutMapping("/{id}/toggle")
    public ResponseEntity<CouponResponseDTO> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.toggleActive(id));
    }
}
