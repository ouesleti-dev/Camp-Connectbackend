package projectexamen.spring.campconnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

    public class offer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long offerId;

        private String title;
        private String description;

        @Temporal(TemporalType.DATE)
        private Date startDate;

        @Temporal(TemporalType.DATE)
        private Date endDate;

        private Double price;

        @Enumerated(EnumType.STRING)
        private OfferStatus status;

        @OneToMany(mappedBy = "offer")
        private List<Contrat> contracts;
}
