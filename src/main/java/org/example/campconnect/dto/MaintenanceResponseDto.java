package org.example.campconnect.dto;

import lombok.Data;
import java.util.Date;

@Data
public class MaintenanceResponseDto {
    private Long   id;
    private Date   startDate;
    private Date   endDate;
    private String description;
    private String kind;
    private Long   equipmentId;
    private String equipmentName;
}