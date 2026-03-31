package org.example.campconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CommentCreateRequest {

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(min = 2, max = 1000, message = "Le commentaire doit contenir entre 2 et 1000 caractères")
    private String content;

    @NotNull(message = "L'ID du post est obligatoire")
    private Long postId;
}