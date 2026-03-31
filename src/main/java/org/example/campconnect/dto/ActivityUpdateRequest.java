package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityUpdateRequest {

    @Size(min = 3, max = 9, message = "Le nom doit contenir entre 3 et 9 caractères")
    private String name;

    @Size(max = 20, message = "La description ne peut pas dépasser 20 caractères")
    private String description;

    @Min(value = 5, message = "La durée minimale est de 5 minutes")
    @Max(value = 1440, message = "La durée maximale est de 1440 minutes (24h)")
    private Integer duration;

    @Pattern(regexp = "EASY|MEDIUM|HARD",
            message = "Difficulté invalide. Valeurs acceptées : EASY, MEDIUM, HARD")
    private String difficulty;

    private Long eventId;
    private Long campingId;
}

