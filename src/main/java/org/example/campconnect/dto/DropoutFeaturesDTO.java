package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DropoutFeaturesDTO {

    @JsonProperty("cancel_rate_user")
    private double cancelRateUser;

    @JsonProperty("activity_difficulty")
    private int activityDifficulty;

    @JsonProperty("nb_participations_user")
    private int nbParticipationsUser;

    private int month;
}