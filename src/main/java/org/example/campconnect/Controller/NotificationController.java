package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.NotificationService;
import org.example.campconnect.dto.NotificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<List<NotificationDto>> getAll(
            Authentication auth) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(auth.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(
            Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "count",
                notificationService.countUnread(auth.getName())));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok().build();
    }
}