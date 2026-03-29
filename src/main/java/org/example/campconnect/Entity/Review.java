package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;
@Builder
@Table(name = "review")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idreview;
    int rating;
    String comment;
    @ManyToOne
    @JoinColumn(name = "user_id_user")
    private User user;
    @ManyToOne
    @JoinColumn(name = "equipment_id_equipement")
    private Equipment equipment;

}
