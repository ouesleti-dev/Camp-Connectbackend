package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(
            String recipientEmail);

    long countByRecipientEmailAndIsReadFalse(
            String recipientEmail);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
            "WHERE n.recipientEmail = :email")
    void markAllAsRead(@Param("email") String email);
}