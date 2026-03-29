package org.example.campconnect.Service;

import org.example.campconnect.Entity.Order;
import org.example.campconnect.dto.OrderRequest;
import org.example.campconnect.dto.OrderResponseDTO;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderRequest request);

    List<OrderResponseDTO> getAllOrders();

    List<OrderResponseDTO> getOrdersByUser(Long userId);

    OrderResponseDTO getOrderById(Long id);

    OrderResponseDTO updateOrderStatus(Long id, String status);

    OrderResponseDTO approveOrder(Long id);

    OrderResponseDTO rejectOrder(Long id);

    List<OrderResponseDTO> getOrdersByStatus(String status);

    void deleteOrder(Long id);
}
