package org.example.campconnect.dto;

import lombok.Builder;
import lombok.Data;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;

import java.util.Date;
@Data
@Builder
public class EquipmentRequestDto {
    private String name;
    private Type type;
    private String description;
    private String owner;
    private Float price;
    private String picture;
}
