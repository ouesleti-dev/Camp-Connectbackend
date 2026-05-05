package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idDelivery;
    String departureAddress;
    String arrivalAddress;
    Double deliveryFee;
    Date estimatedDeliveryDate;
    Double departureLat;
    Double departureLng;
    Double arrivalLat;
    Double arrivalLng;
    @Enumerated(EnumType.STRING)
    DeliveryState deliverystate ;
    @OneToOne
    private Order order;
    @OneToMany(mappedBy = "delivery")
    private List<Reclamation> reclamations;
    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private User deliveryPerson;
}
