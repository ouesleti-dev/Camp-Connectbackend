package org.example.campconnect.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransportAd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long adId;
    float price;
    Long availableSeats;
    @Enumerated(EnumType.STRING)
    TransportType transportType;
    @OneToOne(mappedBy = "transportAd")
    private Reservation reservation;

    @OneToOne
    private Trip trip;
}
