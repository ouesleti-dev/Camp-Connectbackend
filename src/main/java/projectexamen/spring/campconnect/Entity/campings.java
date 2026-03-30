package projectexamen.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /** Cpacité (emplacements / personnes) — alimente l’admin partenariat. */
    private Integer capacite;

    @OneToMany(mappedBy = "camping")
    private List<User> users;

    /** Partenaires métier associés à ce site (gestion partenariat). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "camping_partner",
            joinColumns = @JoinColumn(name = "camping_id"),
            inverseJoinColumns = @JoinColumn(name = "partner_user_id", referencedColumnName = "id_user")
    )
    private Set<User> partnerLinks = new HashSet<>();
}

//
