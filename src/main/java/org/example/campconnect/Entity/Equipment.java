package org.example.campconnect.Entity;


import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    Type type;
    String Description;
    String owner;
    Date aviability;
    Boolean verified;
    @Enumerated(EnumType.STRING)
    State state;
    Float price;
    String picture;
    @ManyToMany (mappedBy = "equipment")
    private List<Rental> rental;
}
