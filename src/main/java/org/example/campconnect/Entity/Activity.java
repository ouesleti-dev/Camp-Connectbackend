package org.example.campconnect.Entity;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer duration;
    private String difficulty;
    @ManyToOne
    private Event event;


    @ManyToOne
    private Camping camping;

    @OneToMany(mappedBy = "activity")
    private List<Participation> participations;




}
