package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long tripId ;
    String departureLocation ;
    String destination ;
    Date departureDate ;
    float distance ;
    @ManyToOne
    private Vehicle vehicle;

    @OneToOne(mappedBy = "trip")
    private TransportAd transportAd;
}
