package tn.esprit.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder


public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idUser;
    String firstName;
    String lastName;
    String email;
    String password;
    String phone;
    @Enumerated(EnumType.STRING)
    Role role;
    @OneToMany(mappedBy = "user")
    private List<Review> review;
    @OneToMany(mappedBy = "user")
    private List<Maintenance> maintenance;
    @OneToMany
    private List<Equipment> equipement;


}
