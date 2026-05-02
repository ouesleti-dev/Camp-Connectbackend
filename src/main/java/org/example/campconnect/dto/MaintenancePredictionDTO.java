package org.example.campconnect.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenancePredictionDTO {
    private Long   equipmentId;
    private String equipmentName;
    private double totalScore;
    private String riskLevel;        // LOW / MEDIUM / HIGH
    private String recommendation;
}