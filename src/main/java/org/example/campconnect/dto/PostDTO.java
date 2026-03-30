package org.example.campconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String content;
    private String createDate;     // format dd/MM/yyyy
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String authorFullName;
}