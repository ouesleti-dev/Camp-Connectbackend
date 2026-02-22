package org.example.campconnect.Entity;

import jakarta.persistence.*;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idUser;
    String firstName;
    String lastName;
    String email;
    String password;
    String phone;
    @Enumerated(EnumType.STRING)
    Role role;


    @OneToMany(mappedBy = "user")
    private List<Participation> participations;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<Response> responses;

    @OneToMany
    private List<Reservation> reservations;

    @OneToMany
    private List<Vehicle> vehicles;

    @OneToMany
    private List<Product> products;
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Review> review;
    @OneToMany(mappedBy = "user")
    private List<Maintenance> maintenance;
    @OneToMany
    private List<Equipment> equipement;



    @OneToMany(mappedBy = "user")
    private List<PartnerInterview> interviews;

    @OneToMany(mappedBy = "user")
    private List<PartnerQuiz> quizzes;





}
