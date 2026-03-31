package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampingCreateRequest {

    @NotBlank(message = "Le nom du camping est obligatoire")
    @Size(min = 3, max = 9, message = "Le nom doit contenir entre 3 et 9 caractères")
    private String name;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 15, message = "L'adresse doit contenir entre 5 et 15 caractères")
    private String address;

    @Size(max = 30, message = "La description ne peut pas dépasser 30 caractères")
    private String description;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "\\d{4,10}", message = "Le code postal doit contenir entre 4 et 10 chiffres")
    private String postalCode;

    @NotBlank(message = "Le statut est obligatoire")
    @Pattern(regexp = "OPEN|CLOSED|MAINTENANCE",
            message = "Statut invalide. Valeurs acceptées : OPEN, CLOSED, MAINTENANCE")
    private String status;
}