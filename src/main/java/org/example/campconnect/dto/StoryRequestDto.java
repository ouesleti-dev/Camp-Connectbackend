package org.example.campconnect.dto;

import lombok.Data;

@Data
public class StoryRequestDto {
    private Long equipmentId;   // ID de l'équipement cible
    private String promoCode;   // ex: "SUMMER20"
    private Float discount;     // ex: 20.0 (= 20%)
    private String message;     // texte affiché sur la story
}