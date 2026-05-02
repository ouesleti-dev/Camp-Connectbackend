package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long vehicleId ;
    String licensePlate ;
    String vehicleType ;
    Long capacity ;
    String status ;
    @OneToMany(mappedBy = "vehicle")
    private List<OptionService> optionServices;

    @OneToMany(mappedBy = "vehicle")
    private List<Trip> trips;
}
