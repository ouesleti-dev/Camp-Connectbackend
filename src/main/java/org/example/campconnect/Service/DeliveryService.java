package org.example.campconnect.Service;


import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.example.campconnect.dto.DeliveryResponseDTO;
import org.example.campconnect.dto.DeliveryStatsDTO;
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

        if (delivery.getDeliverystate() != DeliveryState.PENDING) {
            throw new RuntimeException("Delivery is not available");
        }

        User deliveryPerson = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setDeliverystate(DeliveryState.ON_THE_WAY);
        delivery.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate()); // ✅
        return toDTO(deliveryRepository.save(delivery));
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
        return toDTO(deliveryRepository.save(delivery));
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
                .build();
    }
}