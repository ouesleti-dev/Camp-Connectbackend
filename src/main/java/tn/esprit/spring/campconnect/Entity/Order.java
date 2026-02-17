package tn.esprit.spring.campconnect.Entity;

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
public class Order {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idOrder;
    Date orderDate;
    Double totalAmount;
    String deliveryAddress;
    String paymentMethod;
    @ManyToOne
    private User user;
    @OneToMany
    private List<OrderLine> orderLines;
    @OneToOne(mappedBy = "order")
    private Delivery delivery;
}
