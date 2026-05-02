package org.example.campconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandDecisionDto {
    private Long equipmentId;
    private String equipmentName;
    private Float basePrice;

    // Metrics
    private Long currentRentals;
    private Long previousRentals;
    private Double averageRating;

    // Computed scores
    private Double trend;
    private Double ratingScore;
    private Double demandScore;

    // Decision
    private String prediction;   // GROWING / STABLE / DECLINING
    private String action;       // INCREASE_PRICE / SLIGHT_INCREASE / DECREASE_PRICE / KEEP_PRICE
    private Double suggestedPrice;
    private String trendExplanation;
    private String priceRecommendation;
}