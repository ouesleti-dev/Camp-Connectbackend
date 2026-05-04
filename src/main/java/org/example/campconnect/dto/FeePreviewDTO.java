package org.example.campconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePreviewDTO {
    private Double distanceKm;
    private Double baseFee;
    private Double feePerKm;
    private Double calculatedFee;
    private String breakdown;
}
