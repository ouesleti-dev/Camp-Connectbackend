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
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals;
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Story> stories;
}
