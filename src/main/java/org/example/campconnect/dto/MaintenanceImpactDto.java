package org.example.campconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class MaintenanceImpactDto {
    private int          affectedRentalCount;
    private List<String> affectedEmails;
    private Date         maintenanceStart;
    private Date         maintenanceEnd;
    private String       equipmentName;
}