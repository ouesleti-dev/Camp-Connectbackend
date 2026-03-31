package org.example.campconnect.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private Long id;
    private String name;
    private String description;
    private Integer duration;       // en minutes
    private String difficulty;
    private Long eventId;
    private String eventTitle;
    private Long campingId;
    private String campingName;
    private int totalParticipations; // calculé dynamiquement
}