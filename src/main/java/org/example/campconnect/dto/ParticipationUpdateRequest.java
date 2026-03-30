package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationUpdateRequest {

    @NotBlank(message = "Le statut est obligatoire")
    @Pattern(regexp = "REGISTERED|CONFIRMED|CANCELLED",
            message = "Statut invalide. Valeurs: REGISTERED, CONFIRMED, CANCELLED")
    private String status;
}