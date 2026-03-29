package org.example.campconnect.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate participationDate;
    private String status;


    @ManyToOne
    private Activity activity;

    @ManyToOne
    private User user;

}







