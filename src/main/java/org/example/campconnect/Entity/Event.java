package org.example.campconnect.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate eventDate;
    private Integer maxParticipants;
    private String status;
    private Double wasteCollected;

    @OneToMany(mappedBy = "event")
    private List<Activity> activities;

    @OneToMany(mappedBy = "event")
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "event")
    private List<Post> posts;

    @ManyToOne
    private Camping camping;




}
