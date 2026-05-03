package org.example.campconnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Builder.Default
    ProductStatus productStatus = ProductStatus.PENDING;
    @Enumerated(EnumType.STRING)
    ProductState productState;
    String locationProduct;
    Double latitude;
    Double longitude;
    Date addedDate;
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<OrderLine> orderLines;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
