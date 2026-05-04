package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Notification;
import org.example.campconnect.Repository.NotificationRepository;
import org.example.campconnect.dto.NotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Créer une notification
    private void create(String email, String message, String type,
                        Long equipmentId, String equipmentName,
                        Date start, Date end) {

        notificationRepository.save(
                Notification.builder()
                        .recipientEmail(email)
                        .message(message)
                        .type(type)
                        .isRead(false)
                        .createdAt(new Date())
                        .equipmentId(equipmentId)
                        .equipmentName(equipmentName)
                        .maintenanceStart(start)
                        .maintenanceEnd(end)
                        .build()
        );
    }

    // Notifier les renters affectés
    public void notifyAffectedRenters(List<String> emails,
                                      String equipmentName, Long equipmentId,
                                      Date start, Date end) {

        emails.forEach(email -> create(email,
                String.format(
                        "🚨 Your rental of \"%s\" is affected" +
                                " by maintenance from %tF au %tF.",
                        equipmentName, start, end),
                "MAINTENANCE_AFFECTED",
                equipmentId, equipmentName, start, end));
    }

    // Notifier tous les users
    public void notifyAllUsers(List<String> allEmails,
                               List<String> alreadyNotified,
                               String equipmentName, Long equipmentId,
                               Date start, Date end) {

        allEmails.stream()
                .filter(e -> alreadyNotified.stream()
                        .noneMatch(a -> a.equalsIgnoreCase(e)))
                .forEach(email -> create(email,
                        String.format(
                                "ℹ️ The equipment \"%s\" will be under maintenance from" +
                                        " du %tF au %tF. Rental unavailable.",
                                equipmentName, start, end),
                        "MAINTENANCE_INFO",
                        equipmentId, equipmentName, start, end));
    }

    // Lire mes notifications
    public List<NotificationDto> getMyNotifications(String email) {
        return notificationRepository
                .findByRecipientEmailOrderByCreatedAtDesc(email)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    // Compter non lues
    public long countUnread(String email) {
        return notificationRepository
                .countByRecipientEmailAndIsReadFalse(email);
    }

    // Marquer tout comme lu
    @Transactional
    public void markAllAsRead(String email) {
        notificationRepository.markAllAsRead(email);
    }

    // Supprimer
    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setEquipmentName(n.getEquipmentName());
        dto.setMaintenanceStart(n.getMaintenanceStart());
        dto.setMaintenanceEnd(n.getMaintenanceEnd());
        return dto;
    }
}