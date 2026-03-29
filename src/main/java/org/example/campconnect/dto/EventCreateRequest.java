package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCreateRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 9, message = "Le titre doit contenir entre 3 et 9 caractères")
    private String title;

    @NotNull(message = "La date de l'événement est obligatoire")
    @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
    private java.time.LocalDate eventDate;

    @NotNull(message = "Le nombre maximum de participants est obligatoire")
    @Min(value = 1, message = "Il faut au moins 1 participant")
    @Max(value = 10000, message = "Le maximum est 10 000 participants")
    private Integer maxParticipants;

    @NotBlank(message = "Le statut est obligatoire")
    @Pattern(regexp = "PLANNED|ONGOING|COMPLETED|CANCELLED",
            message = "Statut invalide. Valeurs: PLANNED, ONGOING, COMPLETED, CANCELLED")
    private String status;

    private Double wasteCollected;

    @NotNull(message = "L'ID du camping est obligatoire")
    private Long campingId;
}