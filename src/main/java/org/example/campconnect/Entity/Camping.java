package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Camping {
    @Id
    @GeneratedValue
    private Long campingId;
    private String name;
    private String address;
    private String description;
    private String postalCode;
    private String status;
    private String city;
    private Integer capacite;

    @OneToMany(mappedBy = "camping")
    private List<Event> events;

    @OneToMany(mappedBy = "camping")
    private List<Activity> activities;
    @OneToMany(mappedBy = "camping")
    private List<User> users;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "camping_partner",
            joinColumns = @JoinColumn(name = "camping_id"),
            inverseJoinColumns = @JoinColumn(name = "partner_user_id")
    )
    private Set<User> partnerLinks = new HashSet<>();
}
