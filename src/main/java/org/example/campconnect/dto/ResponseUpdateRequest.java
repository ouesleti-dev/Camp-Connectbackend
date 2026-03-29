package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseUpdateRequest {

    @NotBlank(message = "Le contenu de la réponse est obligatoire")
    @Size(min = 2, max = 1000, message = "La réponse doit contenir entre 2 et 1000 caractères")
    private String content;
}