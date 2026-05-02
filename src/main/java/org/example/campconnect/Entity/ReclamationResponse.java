package org.example.campconnect.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReclamationResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idReclamationResponse;
    String messageResponse;
    Date responseDate;
    @OneToOne(mappedBy = "reclamationResponse")
    private Reclamation reclamation;
}