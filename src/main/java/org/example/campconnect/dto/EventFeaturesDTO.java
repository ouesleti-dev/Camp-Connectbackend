package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventFeaturesDTO {

    @JsonProperty("nb_activities")
    private int nbActivities;

    private int month;

    @JsonProperty("difficulty_score")
    private double difficultyScore;

    @JsonProperty("camping_open")
    private int campingOpen;

    @JsonProperty("history_rate")
    private double historyRate;
}