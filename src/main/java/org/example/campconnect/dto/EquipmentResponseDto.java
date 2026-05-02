package org.example.campconnect.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentResponseDto {
    private Long idEquipement;
    private String name;
    private Type type;
    private String description;
    private String owner;
    private Date aviability;
    private Boolean verified;
    private State state;
    private Float price;
    private String picture;
    private Double averageRating;

    // Constructeur avec Double
    public EquipmentResponseDto(Long idEquipement, String name, Type type,
                                String description, String owner, Date aviability,
                                Boolean verified, State state, Float price,
                                String picture, Double averageRating) {
        this.idEquipement  = idEquipement;
        this.name          = name;
        this.type          = type;
        this.description   = description;
        this.owner         = owner;
        this.aviability    = aviability;
        this.verified      = verified;
        this.state         = state;
        this.price         = price;
        this.picture       = picture;
        this.averageRating = averageRating;
    }

    // Constructeur avec int — pour satisfaire IntelliJ
    public EquipmentResponseDto(Long idEquipement, String name, Type type,
                                String description, String owner, Date aviability,
                                Boolean verified, State state, Float price,
                                String picture, int averageRating) {
        this.idEquipement  = idEquipement;
        this.name          = name;
        this.type          = type;
        this.description   = description;
        this.owner         = owner;
        this.aviability    = aviability;
        this.verified      = verified;
        this.state         = state;
        this.price         = price;
        this.picture       = picture;
        this.averageRating = (double) averageRating;
    }
}