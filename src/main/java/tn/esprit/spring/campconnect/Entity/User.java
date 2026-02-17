package tn.esprit.spring.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
@OneToMany
    private List<Product> products;
@OneToMany(mappedBy = "user")
    private List<Order> orders;
}
