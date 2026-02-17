package tn.esprit.spring.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idreview;
    String rating;
    String comment;
    @ManyToOne
    private User user;
    @ManyToOne
    private Equipment equipment;

}
