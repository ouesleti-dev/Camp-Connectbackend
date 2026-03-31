package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    private String title;
    private String description;


    private Date startDate;


    private Date endDate;

    private Double price;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @OneToMany(mappedBy = "offer")
    private List<Contrat> contracts;
}