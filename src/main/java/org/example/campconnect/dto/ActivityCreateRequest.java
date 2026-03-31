package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityCreateRequest {

    @NotBlank(message = "Le nom de l'activité est obligatoire")
    @Size(min = 3, max = 9, message = "Le nom doit contenir entre 3 et 9 caractères")
    private String name;

    @Size(max =30, message = "La description ne peut pas dépasser 30 caractères")
    private String description;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 5, message = "La durée minimale est de 5 minutes")
    @Max(value = 1440, message = "La durée maximale est de 1440 minutes (24h)")
    private Integer duration;

    @NotBlank(message = "La difficulté est obligatoire")
    @Pattern(regexp = "EASY|MEDIUM|HARD",
            message = "Difficulté invalide. Valeurs acceptées : EASY, MEDIUM, HARD")
    private String difficulty;

    // Une activité est liée à un Event ET/OU un Camping
    // Au moins un des deux doit être fourni
    private Long eventId;
    private Long campingId;
}