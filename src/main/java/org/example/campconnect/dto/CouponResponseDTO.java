package org.example.campconnect.dto;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    private Long idCoupon;
    private String code;
    private Double discountPercentage;
    private Date expirationDate;
    private Integer maxUses;
    private Integer currentUses;
    private Boolean active;
    private boolean valid; // used for validation response
    private String message;
}
