package org.example.campconnect.dto;

import java.util.Date;

public record ReservationResponse(
        Long reservationId,
        Date reservationDate,
        Long seatCount,
        String status,
        Long transportAdId,
        float adPrice,
        String departureLocation,
        String destination,
        String userEmail
) {}
