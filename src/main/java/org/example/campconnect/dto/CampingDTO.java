package org.example.campconnect.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CampingDTO {
    private Long campingId;
    private String name;
    private String address;
    private String description;
    private String postalCode;
    private String status;
    private int totalEvents;      // nombre d'events liés (info utile en lecture)
    private int totalActivities;  // nombre d'activités liées
}