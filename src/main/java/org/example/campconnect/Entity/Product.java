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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idProduct;
    String nameProduct;
    @Enumerated(EnumType.STRING)
    Category category;
    String descriptionProduct;
    Double priceProduct;
    Integer quantityProduct;

    @Column(columnDefinition = "LONGTEXT")
    String photoProduct;
    @Enumerated(EnumType.STRING)
    ProductState productState;
    String locationProduct;
    Date addedDate;
    @OneToMany(mappedBy = "product")
    private List<OrderLine> orderLines;
}
