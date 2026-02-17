package tn.esprit.spring.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idReclamation;
    String subject;
    String descriptionReclamation;
    Date dateReclamation;
    @Enumerated(EnumType.STRING)
    ReclamationState reclamationstate ;
    @ManyToOne
    private Delivery delivery;
    @OneToOne
    private ReclamationResponse reclamationResponse;

}
