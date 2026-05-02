package org.example.campconnect.dto;

import lombok.*;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentSearchDto {
    private Type type;
    private State state;
    private Float maxPrice;
}