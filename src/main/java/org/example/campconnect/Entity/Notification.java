package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String message;

    private String type;
    private String recipientEmail;

    @Builder.Default
    private Boolean isRead = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private Date createdAt = new Date();

    private Long   equipmentId;
    private String equipmentName;
    private Date   maintenanceStart;
    private Date   maintenanceEnd;
}
