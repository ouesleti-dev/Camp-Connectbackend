package org.example.campconnect.Service;

import org.example.campconnect.Entity.Notification;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.NotificationRepository;
import org.example.campconnect.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyStatusChange(Long userId, String message) {
        sendNotification(userId, message, "STATUS_CHANGE");
    }

    public void notifyInterviewScheduled(Long userId, String message) {
        sendNotification(userId, message, "INTERVIEW");
    }

    public void notifyContractExpired(Long userId, String message) {
        sendNotification(userId, message, "CONTRACT_EXPIRATION");
    }

    private void sendNotification(Long userId, String message, String type) {
        // 1. Sauvegarder dans la DB
        Notification notif = Notification.builder()
                .recipientId(userId)
                .message(message)
                .type(type)
                .channel("IN_APP_AND_EMAIL")
                .isRead(false)
                .build();
        notif = notificationRepository.save(notif);

        // Chercher l'utilisateur pour l'email (utilisé par l'email ET le websocket user-destination)
        User user = userRepository.findById(userId).orElse(null);

        // 2. Envoyer en temps réel via WebSocket
        // destination: /user/queue/notifications (Spring s'occupe de router vers l'utilisateur connecté)
        if (user != null && user.getEmail() != null) {
            messagingTemplate.convertAndSendToUser(user.getEmail(), "/queue/notifications", notif);

            // 3. Envoyer par email
            String subject = "CampConnect Notification: " + type;
            emailService.sendEmail(user.getEmail(), subject, message);
        }
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
