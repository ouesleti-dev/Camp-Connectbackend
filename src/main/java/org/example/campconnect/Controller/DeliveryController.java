package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.DeliveryFeeService;
import org.example.campconnect.Service.IDeliveryService;
import org.example.campconnect.dto.DeliveryResponseDTO;
import org.example.campconnect.dto.DeliveryStatsDTO;
import org.example.campconnect.dto.FeePreviewDTO;
import org.example.campconnect.dto.TakeDeliveryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;
    private final DeliveryFeeService deliveryFeeService;


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
    @GetMapping("/stats/top-performers")
    public ResponseEntity<List<DeliveryStatsDTO>> getTopPerformers() {
        return ResponseEntity.ok(deliveryService.getTopDeliveryPersonStats());
    }

    @GetMapping("/customer/{customerId}/active")
    public ResponseEntity<List<DeliveryResponseDTO>> getActiveForCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesForCustomer(customerId));
    }

    @GetMapping("/fee-preview")
    public ResponseEntity<FeePreviewDTO> previewFee(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam(required = false) Double fromLat,
            @RequestParam(required = false) Double fromLng,
            @RequestParam(required = false) Double toLat,
            @RequestParam(required = false) Double toLng) {

        FeePreviewDTO dto;

        // Si les coords sont fournies → calcul direct, zéro Nominatim
        if (fromLat != null && fromLng != null && toLat != null && toLng != null) {
            dto = deliveryFeeService.calculateFeeFromCoords(
                    fromLat, fromLng, toLat, toLng, departure, arrival
            );
        } else {
            // Fallback géocodage (lent, risque 429)
            dto = deliveryFeeService.calculateFeePreview(departure, arrival);
        }

        return ResponseEntity.ok(dto);
    }
}
