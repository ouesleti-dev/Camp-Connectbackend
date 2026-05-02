package org.example.campconnect.dto;

import lombok.*;
import org.example.campconnect.Entity.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentStatsDto {
    private Long idEquipement;
    private String name;
    private Type type;
    private Float price;
    private Double averageRating;   // moyenne des reviews
    private Long reviewCount;        // nombre de reviews
    private Long totalRentals;       // nombre de locations
}