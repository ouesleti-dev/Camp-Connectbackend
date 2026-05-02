package org.example.campconnect.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
    private int rating;      // 1 à 5
    private String comment;
}