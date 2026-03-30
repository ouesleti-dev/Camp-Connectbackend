package org.example.campconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    private Long id;
    private String content;
    private String createDate;
    private Long commentId;
    private Long userId;
    private String authorFullName;
}