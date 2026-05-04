package org.example.campconnect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class MaintenanceConfirmDto {
    private Long   equipmentId;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date   startDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")  //
    private Date   endDate;
    private String description;
    private String kind;
}