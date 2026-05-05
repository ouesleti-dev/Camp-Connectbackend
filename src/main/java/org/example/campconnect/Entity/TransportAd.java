package org.example.campconnect.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @JsonIgnore
    @OneToMany(mappedBy = "transportAd")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToOne
    private Trip trip;
}
