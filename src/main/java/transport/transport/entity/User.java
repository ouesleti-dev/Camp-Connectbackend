package transport.transport.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
@Entity
@Getter
@Setter
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
    @Enumerated (EnumType.STRING)
    Role role;
    @OneToMany
    private List<Reservation> reservations;

    @OneToMany
    private List<Vehicle> vehicles;
}
