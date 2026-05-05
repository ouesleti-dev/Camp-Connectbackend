package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.NotificationDeliveryService;
import org.example.campconnect.dto.NotificationDeliveryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationDeliveryController {

    private final NotificationDeliveryService notificationDeliveryService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDeliveryDTO>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationDeliveryService.getUserNotifications(userId));
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationDeliveryDTO>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationDeliveryService.getUnreadNotifications(userId));
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationDeliveryService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationDeliveryService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationDeliveryService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
