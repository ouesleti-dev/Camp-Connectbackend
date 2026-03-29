package org.example.campconnect.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentDTO {
    private Long id;
    private String content;
    private String createDate;    // format dd/MM/yyyy
    private Long postId;
    private Long userId;
    private String authorFullName;
}