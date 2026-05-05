package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Order;
import org.example.campconnect.Service.IOrderService;
import org.example.campconnect.Service.OrderService;
import org.example.campconnect.dto.OrderRequest;
import org.example.campconnect.dto.OrderResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(orderService.getOrderById(order.getIdOrder()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, "CONFIRMED"));
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, "CANCELLED"));
    }


    @PutMapping("/{id}/approve")
    public ResponseEntity<OrderResponseDTO> approveOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.approveOrder(id));
    }


    @PutMapping("/{id}/reject")
    public ResponseEntity<OrderResponseDTO> rejectOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, "REJECTED"));
    }


    @GetMapping("/confirmed")
    public ResponseEntity<List<OrderResponseDTO>> getConfirmedOrders() {
        return ResponseEntity.ok(orderService.getOrdersByStatus("CONFIRMED"));
    }
}
