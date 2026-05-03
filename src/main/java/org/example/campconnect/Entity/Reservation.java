package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long reservationId ;
    Date reservationDate ;
    Long seatCount ;
    String status ;
    Float totalPrice;

    @ManyToMany
    @JoinTable(
            name = "reservation_options",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<OptionService> selectedOptions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "transport_ad_id")
    private TransportAd transportAd;
}
