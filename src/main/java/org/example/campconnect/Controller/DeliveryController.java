package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.DeliveryService;
import org.example.campconnect.Service.IDeliveryService;
import org.example.campconnect.dto.DeliveryResponseDTO;
import org.example.campconnect.dto.TakeDeliveryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;


    @GetMapping("/pending")
    public ResponseEntity<List<DeliveryResponseDTO>> getPending() {
        return ResponseEntity.ok(deliveryService.getPendingDeliveries());
    }


    @GetMapping("/my/{userId}")
    public ResponseEntity<List<DeliveryResponseDTO>> getMyDeliveries(
            @PathVariable Long userId) {
        return ResponseEntity.ok(deliveryService.getMyDeliveries(userId));
    }


    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> getAll() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }


    @PutMapping("/{deliveryId}/take")
    public ResponseEntity<DeliveryResponseDTO> take(
            @PathVariable Long deliveryId,
            @RequestBody TakeDeliveryRequest request) {
        return ResponseEntity.ok(deliveryService.takeDelivery(deliveryId, request));
    }


    @PutMapping("/{deliveryId}/deliver/{userId}")
    public ResponseEntity<DeliveryResponseDTO> deliver(
            @PathVariable Long deliveryId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(deliveryService.markDelivered(deliveryId, userId));
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryResponseDTO>> getByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByOrder(orderId));
    }
}
