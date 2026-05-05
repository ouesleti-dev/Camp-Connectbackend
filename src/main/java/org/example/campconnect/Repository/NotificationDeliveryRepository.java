package org.example.campconnect.Repository;

import org.example.campconnect.Entity.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    List<NotificationDelivery> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationDelivery> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadFalse(Long userId);
}
