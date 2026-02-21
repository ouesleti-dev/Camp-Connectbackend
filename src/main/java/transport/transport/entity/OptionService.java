package transport.transport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long optionId ;
    String name ;
    @Enumerated(EnumType.STRING)
    OptionType optionType ;
    @ManyToOne
    private Vehicle vehicle;
}
