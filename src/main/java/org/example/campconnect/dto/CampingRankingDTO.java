package org.example.campconnect.dto;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CampingRankingDTO {
    private Long campingId;
    private String campingName;
    private String campingStatus;

    // Agrégations multi-tables
    private long totalEvents;
    private long totalActivities;
    private long totalParticipations; // JOIN Event→Activity→Participation
    private long totalPosts;          // JOIN Event→Post
    private long totalTickets;        // JOIN Event→Ticket
    private double totalWasteCollected; // SUM sur Event.wasteCollected
    private double avgFillRate;       // AVG taux remplissage
    private int engagementScore;      // score calculé
}