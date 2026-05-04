package org.example.campconnect.dto;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketDTO {
    private Long id;
    private String ticketCode;
    private String issueDate;
    private String status;
    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private Long userId;
    private String participantFullName;
    private String qrCodeBase64; // ⭐ image QR en base64 pour afficher dans Angular
}