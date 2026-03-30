package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdUser(Long userId);
    List<Order> findByOrderStatus(String status);

}