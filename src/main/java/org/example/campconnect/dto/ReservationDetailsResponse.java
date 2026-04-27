package org.example.campconnect.dto;

import org.example.campconnect.Entity.TransportType;

import java.util.Date;

public record ReservationDetailsResponse(
        Long reservationId,
        Date reservationDate,
        Long seatCount,
        String status,
        float adPrice,
        TransportType transportType,
        String destination,
        String vehicleLicensePlate
) {}
