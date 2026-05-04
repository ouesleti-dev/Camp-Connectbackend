package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DropoutPredictionDTO {

    @JsonProperty("dropout_probability")
    private double dropoutProbability;

    @JsonProperty("risk_level")
    private String riskLevel;  // LOW / MEDIUM / HIGH
}