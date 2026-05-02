package org.example.campconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponseDto {
    private Long idStory;
    private Long equipmentId;
    private String equipmentName;
    private String ownerEmail;
    private Float originalPrice;
    private Float discountedPrice;  // prix après réduction, calculé à la volée
    private Float discount;         // pourcentage
    private String promoCode;
    private String message;
    private Boolean active;
    private Date createdAt;
    private Date expiresAt;
    private Long minutesRemaining;  // TTL en minutes pour le frontend
}