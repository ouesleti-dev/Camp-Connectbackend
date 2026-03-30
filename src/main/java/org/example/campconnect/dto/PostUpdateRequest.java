package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    @NotBlank(message = "Le contenu du post est obligatoire")
    @Size(min = 5, max = 2000, message = "Le contenu doit contenir entre 5 et 2000 caractères")
    private String content;
}