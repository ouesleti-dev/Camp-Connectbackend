package org.example.campconnect.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RentalRequestDto {
    private Long equipmentId;
    private Date startDate;
    private Date endDate;
}
