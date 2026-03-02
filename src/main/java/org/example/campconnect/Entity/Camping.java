package org.example.campconnect.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @OneToMany(mappedBy = "camping")
    private List<Event> events;

    @OneToMany(mappedBy = "camping")
    private List<Activity> activities;
}
