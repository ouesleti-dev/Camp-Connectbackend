package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventPredictionDTO {

    @JsonProperty("predicted_participants")
    private int predictedParticipants;

    private String confidence;
}