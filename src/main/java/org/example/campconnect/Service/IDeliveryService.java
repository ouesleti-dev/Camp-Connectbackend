package org.example.campconnect.Service;

import org.example.campconnect.dto.DeliveryResponseDTO;
import org.example.campconnect.dto.DeliveryStatsDTO;
import org.example.campconnect.dto.TakeDeliveryRequest;

import java.util.List;

public interface IDeliveryService {
    List<DeliveryResponseDTO> getPendingDeliveries();
    List<DeliveryResponseDTO> getMyDeliveries(Long userId);
    List<DeliveryResponseDTO> getAllDeliveries();
    DeliveryResponseDTO takeDelivery(Long deliveryId, TakeDeliveryRequest request);
    DeliveryResponseDTO markDelivered(Long deliveryId, Long userId);
    List<DeliveryResponseDTO> getDeliveriesByOrder(Long orderId);
    List<DeliveryStatsDTO> getTopDeliveryPersonStats();
    List<DeliveryResponseDTO> getDeliveriesForCustomer(Long customerId);
}