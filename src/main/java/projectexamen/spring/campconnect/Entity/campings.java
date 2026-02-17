package projectexamen.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "campings")
public class campings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campingId;

    private String name;
    private String address;
    private String city;
    private String postalCode;

    @OneToMany(mappedBy = "camping")
    private List<User> users;}
