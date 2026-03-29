package org.example.campconnect.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String title;
    private String eventDate;      // format dd/MM/yyyy
    private Integer maxParticipants;
    private String status;
    private Double wasteCollected;
    private Long campingId;
    private String campingName;
}