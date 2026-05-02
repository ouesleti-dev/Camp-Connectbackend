package org.example.campconnect.dto;

import lombok.Data;

@Data
public class RecommendationRequestDto {
    private String place;
    private String season;
    private int people;
    private int duration_days;
    private double budget;
}
