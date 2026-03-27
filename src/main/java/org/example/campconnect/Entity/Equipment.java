package org.example.campconnect.Entity;

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
    @Enumerated(EnumType.STRING)
    Type type;
    String description;
    String owner;
    Date aviability;
    @Column(nullable = false)
    @Builder.Default
    Boolean verified = false;
    @Enumerated(EnumType.STRING)
    State state;
    Float price;
    @Column(columnDefinition = "LONGTEXT")
    String picture;
    @ManyToMany (mappedBy = "equipment")
    private List<Rental> rental;
}
