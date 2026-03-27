package org.example.campconnect.dto;

import lombok.Data;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;

import java.util.Date;

@Data
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
}
