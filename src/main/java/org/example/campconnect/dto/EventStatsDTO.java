package org.example.campconnect.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventStatsDTO {
    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private String status;
    private String campingName;

    // Stats activités
    private long totalActivities;
    private long easyActivities;
    private long mediumActivities;
    private long hardActivities;

    // Stats participations
    private long totalParticipations;
    private int maxParticipants;
    private double fillRate; // totalParticipations / maxParticipants * 100

    // Stats forum
    private long totalPosts;
    private long totalComments;

    // Stats tickets
    private long totalTickets;
    private long validTickets;
    private long usedTickets;
    private long cancelledTickets;
}