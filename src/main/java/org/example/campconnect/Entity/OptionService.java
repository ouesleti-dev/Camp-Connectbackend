package org.example.campconnect.Entity;


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
    Float price;
    @Enumerated(EnumType.STRING)
    OptionType optionType ;
    @ManyToOne
    private Vehicle vehicle;
}
