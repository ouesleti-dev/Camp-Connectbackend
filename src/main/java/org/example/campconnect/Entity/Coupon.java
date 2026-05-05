package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCoupon;

    @Column(unique = true, nullable = false)
    private String code;

    private Double discountPercentage; // ex: 20.0 = 20%

    private Date expirationDate;

    private Integer maxUses;

    @Builder.Default
    private Integer currentUses = 0;

    @Builder.Default
    private Boolean active = true;
}
