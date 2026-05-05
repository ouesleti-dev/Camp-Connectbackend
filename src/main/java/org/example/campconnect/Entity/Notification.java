package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'ID du partenaire/utilisateur concerné
    private Long recipientId;

    private String message;

    // Type de notification: STATUS_CHANGE, INTERVIEW, CONTRACT_EXPIRATION
    private String type;

    // Canal d'envoi: IN_APP, EMAIL
    private String channel;

    private boolean isRead;

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
