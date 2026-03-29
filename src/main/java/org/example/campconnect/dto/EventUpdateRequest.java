package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateRequest {

    @Size(min = 3, max = 9, message = "Le titre doit contenir entre 3 et 9 caractères")
    private String title;

    @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
    private java.time.LocalDate eventDate;

    @Min(value = 1, message = "Il faut au moins 1 participant")
    @Max(value = 10000, message = "Le maximum est 10 000 participants")
    private Integer maxParticipants;

    @Pattern(regexp = "PLANNED|ONGOING|COMPLETED|CANCELLED",
            message = "Statut invalide. Valeurs: PLANNED, ONGOING, COMPLETED, CANCELLED")
    private String status;

    private Double wasteCollected;

    private Long campingId;
}
