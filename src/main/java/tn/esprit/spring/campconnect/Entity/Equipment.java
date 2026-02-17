package tn.esprit.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idEquipement;
    String name;
    Type type;
    String Description;
    String owner;
    Date aviability;
    Boolean verified;
    State state;
    Float price;
    String picture;
    @ManyToMany (mappedBy = "equipment")
    private List<Rental> rental;
}
