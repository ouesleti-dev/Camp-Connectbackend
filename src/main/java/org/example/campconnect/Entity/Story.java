package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "story")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStory;

    private String promoCode;       // ex: "SUMMER20"

    private Float discount;         // ex: 20.0 = 20%

    private String message;         // texte affiché sur la story

    @Builder.Default
    private Boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;         // createdAt + 24h

    // Relation : une story appartient à UN équipement
    // Un équipement ne peut avoir qu'une story active à la fois (vérifié en service)
    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    // Relation : la story est créée par UN user (le propriétaire)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}