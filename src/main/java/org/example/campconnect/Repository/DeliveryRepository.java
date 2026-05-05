package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Delivery;
import org.example.campconnect.Entity.DeliveryState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByDeliverystate(DeliveryState state);
    List<Delivery> findByDeliveryPersonIdUser(Long userId);
    List<Delivery> findByOrderIdOrder(Long orderId);
    List<Delivery> findByDeliveryPersonIdUserAndDeliverystate(Long userId, DeliveryState state);
    List<Delivery> findByDeliverystateAndOrderUserIdUser(DeliveryState state, Long customerId);
}
