package org.example.campconnect.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RentalResponseDto {
    private Long rentalId;
    private Date startDate;
    private Date endDate;
    private Float totalAmount;
    private Boolean verified;
    private String renterEmail;
    private String ownerEmail;
    private Long equipmentId;
    private String equipmentName;
}
