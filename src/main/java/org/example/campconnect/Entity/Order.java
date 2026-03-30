package org.example.campconnect.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Table(name = "orders")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idOrder;
    Date orderDate;
    Double totalAmount;
    String deliveryAddress;
    String paymentMethod;
    @Column(nullable = false)
    @Builder.Default
    String orderStatus = "PENDING";
    @ManyToOne
    private User user;

    @OneToMany(fetch = FetchType.EAGER)
    private List<OrderLine> orderLines;
    @JsonIgnore
    @OneToOne(mappedBy = "order")
    private Delivery delivery;
}
