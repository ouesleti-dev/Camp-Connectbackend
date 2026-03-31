package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;


    private Date startDate;


    private Date endDate;

    private Double commission;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private offer offer;

}