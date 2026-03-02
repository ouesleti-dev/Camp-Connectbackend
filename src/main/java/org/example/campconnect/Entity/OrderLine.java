package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idOrderLine;
    int requestedQuantity;
    Double unitPrice;
    Double totalPrice;
    @ManyToOne
    private Product product;
}