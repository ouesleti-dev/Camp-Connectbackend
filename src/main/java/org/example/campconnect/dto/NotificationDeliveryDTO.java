package org.example.campconnect.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDeliveryDTO {
    private Long idNotification;
    private String message;
    private String type;
    private Long userId;
    private Long deliveryId;
    private boolean read;
    private LocalDateTime createdAt;
}
