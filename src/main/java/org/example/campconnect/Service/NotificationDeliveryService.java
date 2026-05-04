package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campconnect.Entity.NotificationDelivery;
import org.example.campconnect.Repository.NotificationDeliveryRepository;
import org.example.campconnect.dto.NotificationDeliveryDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDeliveryService {

    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, Long deliveryId, String type, String message) {
        NotificationDelivery notification = NotificationDelivery.builder()
                .userId(userId)
                .deliveryId(deliveryId)
                .type(type)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationDeliveryRepository.save(notification);
        log.info("Notification saved for user {}: {}", userId, message);

        NotificationDeliveryDTO dto = toDTO(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, dto);
        log.info("WebSocket notification sent to /topic/notifications/{}", userId);
    }

    @Transactional(readOnly = true)
    public List<NotificationDeliveryDTO> getUserNotifications(Long userId) {
        return notificationDeliveryRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDeliveryDTO> getUnreadNotifications(Long userId) {
        return notificationDeliveryRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationDeliveryRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationDeliveryRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationDeliveryRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<NotificationDelivery> unread =
                notificationDeliveryRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationDeliveryRepository.saveAll(unread);
    }

    private NotificationDeliveryDTO toDTO(NotificationDelivery n) {
        return NotificationDeliveryDTO.builder()
                .idNotification(n.getIdNotification())
                .message(n.getMessage())
                .type(n.getType())
                .userId(n.getUserId())
                .deliveryId(n.getDeliveryId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
