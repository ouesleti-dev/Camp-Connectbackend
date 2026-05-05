package org.example.campconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceSlotDto {
    private Date start;
    private Date end;
    private long bufferDaysAfter;
}