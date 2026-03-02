package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idmaintenance;
    @Enumerated(EnumType.STRING)
    Kind kind;
    String description;
    Date startdate;
    Date enddate;
    @ManyToOne
    private User user;
    @ManyToOne
    private Equipment equipment;
}
