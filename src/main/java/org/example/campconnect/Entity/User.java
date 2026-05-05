package org.example.campconnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.ArrayList;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.List;
@Builder
@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idUser;
    String firstName;
    String lastName;
    @Column(nullable = false, unique = true)
    String email;
    @Column(nullable = false)
    String password;
    String phone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;
    @Builder.Default
    boolean enabled = true;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Participation> participations;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Response> responses;
    @JsonIgnore
    @OneToMany
    @JoinTable(
            name = "user_reservations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "reservations_reservation_id")
    )
    private List<Reservation> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany
    private List<Vehicle> vehicles = new ArrayList<>();

    @JoinTable(
            name = "user_vehicles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicles_vehicle_id")
    )

    @JsonIgnore
    @OneToMany
    private List<Product> products;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Review> review;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Maintenance> maintenance;
    @JsonIgnore
    @OneToMany
    private List<Equipment> equipement;


    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PartnerInterview> interviews;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PartnerQuiz> quizzes;
    @OneToMany(mappedBy = "user")
    private List<Story> stories;




}
