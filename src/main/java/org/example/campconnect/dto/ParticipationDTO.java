package org.example.campconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationDTO {
    private Long id;
    private String participationDate;   // format dd/MM/yyyy
    private String status;
    private Long activityId;
    private String activityName;
    private Long userId;
    private String participantFullName;
}