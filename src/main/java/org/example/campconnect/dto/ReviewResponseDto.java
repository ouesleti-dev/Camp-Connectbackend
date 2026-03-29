package org.example.campconnect.dto;

import lombok.Data;

@Data
public class ReviewResponseDto {
    private Long idreview;
    private int rating;
    private String comment;
    private String userEmail;
    private Long equipmentId;
}