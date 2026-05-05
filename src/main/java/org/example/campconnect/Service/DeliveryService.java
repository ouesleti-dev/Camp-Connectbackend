package org.example.campconnect.Service;


import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.example.campconnect.dto.DeliveryResponseDTO;
import org.example.campconnect.dto.DeliveryStatsDTO;
import org.example.campconnect.dto.FeePreviewDTO;
import org.example.campconnect.dto.TakeDeliveryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final DeliveryFeeService deliveryFeeService;
    private final NotificationDeliveryService notificationDeliveryService;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getPendingDeliveries() {
        return deliveryRepository.findByDeliverystate(DeliveryState.PENDING)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getMyDeliveries(Long userId) {
        return deliveryRepository.findByDeliveryPersonIdUser(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryResponseDTO takeDelivery(Long deliveryId, TakeDeliveryRequest request) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (delivery.getDeliverystate() != DeliveryState.PENDING)
            throw new RuntimeException("Delivery is not available");

        User deliveryPerson = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setDeliverystate(DeliveryState.ON_THE_WAY);
        delivery.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());

        // ── Calcul fee : coords directes si dispo, sinon géocodage ──
        Order order = delivery.getOrder();
        FeePreviewDTO feeDTO;

        if (order != null
                && order.getDeliveryLat() != null
                && order.getDeliveryLng() != null
                && delivery.getDepartureLat() != null
                && delivery.getDepartureLng() != null) {

            // ✅ Coords disponibles → zéro appel Nominatim
            feeDTO = deliveryFeeService.calculateFeeFromCoords(
                    delivery.getDepartureLat(), delivery.getDepartureLng(),
                    order.getDeliveryLat(),     order.getDeliveryLng(),
                    delivery.getDepartureAddress(), delivery.getArrivalAddress()
            );
        } else {
            // Fallback géocodage
            feeDTO = deliveryFeeService.calculateFeePreview(
                    delivery.getDepartureAddress(), delivery.getArrivalAddress()
            );
        }

        delivery.setDeliveryFee(feeDTO.getCalculatedFee());
        // ─────────────────────────────────────────────────────────────

        DeliveryResponseDTO result = toDTO(deliveryRepository.save(delivery));

        if (order != null && order.getUser() != null) {
            String dpName = deliveryPerson.getFirstName() + " " + deliveryPerson.getLastName();
            notificationDeliveryService.sendNotification(
                    order.getUser().getIdUser(), delivery.getIdDelivery(),
                    "DELIVERY_TAKEN",
                    "Your order #" + order.getIdOrder() + " assigned to " + dpName
            );
        }

        return result;
    }

    @Override
    @Transactional
    public DeliveryResponseDTO markDelivered(Long deliveryId, Long userId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (delivery.getDeliveryPerson() == null ||
                !delivery.getDeliveryPerson().getIdUser().equals(userId)) {
            throw new RuntimeException("You are not assigned to this delivery");
        }

        delivery.setDeliverystate(DeliveryState.DELIVERED);
        DeliveryResponseDTO result = toDTO(deliveryRepository.save(delivery));

        if (delivery.getOrder() != null && delivery.getOrder().getUser() != null) {
            Long customerId = delivery.getOrder().getUser().getIdUser();
            notificationDeliveryService.sendNotification(
                    customerId,
                    delivery.getIdDelivery(),
                    "DELIVERY_COMPLETED",
                    "Your order #" + delivery.getOrder().getIdOrder()
                            + " has been delivered successfully!"
            );
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getDeliveriesByOrder(Long orderId) {
        return deliveryRepository.findByOrderIdOrder(orderId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryStatsDTO> getTopDeliveryPersonStats() {
        // Uses keyword query: group by delivery person, count per state
        return deliveryRepository.findAll().stream()
                .filter(d -> d.getDeliveryPerson() != null)
                .collect(Collectors.groupingBy(d -> d.getDeliveryPerson()))
                .entrySet().stream()
                .map(entry -> {
                    User person = entry.getKey();
                    List<Delivery> deliveries = entry.getValue();
                    return DeliveryStatsDTO.builder()
                            .deliveryPersonId(person.getIdUser())
                            .deliveryPersonName(person.getFirstName() + " " + person.getLastName())
                            .deliveryPersonPhone(person.getPhone())
                            .completedDeliveries(deliveries.stream()
                                    .filter(d -> d.getDeliverystate() == DeliveryState.DELIVERED).count())
                            .pendingDeliveries(deliveries.stream()
                                    .filter(d -> d.getDeliverystate() == DeliveryState.PENDING).count())
                            .cancelledDeliveries(deliveries.stream()
                                    .filter(d -> d.getDeliverystate() == DeliveryState.CANCELLED).count())
                            .onTheWayDeliveries(deliveries.stream()
                                    .filter(d -> d.getDeliverystate() == DeliveryState.ON_THE_WAY).count())
                            .build();
                })
                // Only return delivery persons with at least 1 completed delivery
                .filter(s -> s.getCompletedDeliveries() > 0)
                .sorted(Comparator.comparingLong(DeliveryStatsDTO::getCompletedDeliveries).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getDeliveriesForCustomer(Long customerId) {
        // Uses keyword query: Delivery → Order → User (customer)
        return deliveryRepository
                .findByDeliverystateAndOrderUserIdUser(DeliveryState.ON_THE_WAY, customerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private DeliveryResponseDTO toDTO(Delivery d) {
        return DeliveryResponseDTO.builder()
                .idDelivery(d.getIdDelivery())
                .departureAddress(d.getDepartureAddress())
                .arrivalAddress(d.getArrivalAddress())
                .deliveryFee(d.getDeliveryFee())
                .deliveryState(d.getDeliverystate() != null ?
                        d.getDeliverystate().name() : null)
                .orderId(d.getOrder() != null ? d.getOrder().getIdOrder() : null)
                .orderDeliveryAddress(d.getOrder() != null ?
                        d.getOrder().getDeliveryAddress() : null)
                .orderTotalAmount(d.getOrder() != null ?
                        d.getOrder().getTotalAmount() : null)
                .orderDate(d.getOrder() != null ? d.getOrder().getOrderDate() : null)
                .customerFirstName(d.getOrder() != null && d.getOrder().getUser() != null ?
                        d.getOrder().getUser().getFirstName() : null)
                .customerLastName(d.getOrder() != null && d.getOrder().getUser() != null ?
                        d.getOrder().getUser().getLastName() : null)
                .customerEmail(d.getOrder() != null && d.getOrder().getUser() != null ?
                        d.getOrder().getUser().getEmail() : null)
                .deliveryPersonId(d.getDeliveryPerson() != null ?
                        d.getDeliveryPerson().getIdUser() : null)
                .deliveryPersonFirstName(d.getDeliveryPerson() != null ?
                        d.getDeliveryPerson().getFirstName() : null)
                .deliveryPersonLastName(d.getDeliveryPerson() != null ?
                        d.getDeliveryPerson().getLastName() : null)
                .estimatedDeliveryDate(d.getEstimatedDeliveryDate())
                .customerPhone(d.getOrder() != null && d.getOrder().getUser() != null ?
                        d.getOrder().getUser().getPhone() : null)
                .deliveryPersonPhone(d.getDeliveryPerson() != null ?
                        d.getDeliveryPerson().getPhone() : null)
                .departureLat(d.getDepartureLat())
                .departureLng(d.getDepartureLng())
                .arrivalLat(d.getOrder() != null ? d.getOrder().getDeliveryLat() : null)
                .arrivalLng(d.getOrder() != null ? d.getOrder().getDeliveryLng() : null)
                .build();
    }
}