package org.example.campconnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @OneToOne(mappedBy = "trip")
    private TransportAd transportAd;
}
