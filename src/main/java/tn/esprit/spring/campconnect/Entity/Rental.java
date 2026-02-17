package tn.esprit.spring.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long rentalid;
    Date startdate;
    Date enddate;
    float totalAmount;
    Boolean verified;
    @ManyToMany
    private List<Equipment> equipment;
}
