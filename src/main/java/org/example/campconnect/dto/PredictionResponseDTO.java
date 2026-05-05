package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictionResponseDTO {
    @JsonProperty("candidate_score")
    private Double candidateScore;

    @JsonProperty("recommendation")
    private String recommendation;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("model_version")
    private String modelVersion;
}
