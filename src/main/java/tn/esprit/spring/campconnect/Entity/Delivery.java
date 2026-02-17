package tn.esprit.spring.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Enumerated(EnumType.STRING)
    DeliveryState deliverystate ;
    @OneToOne
    private Order order;
    @OneToMany(mappedBy = "delivery")
    private List<Reclamation> reclamations;
}
