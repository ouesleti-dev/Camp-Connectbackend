package org.example.campconnect.dto;

import java.util.Date;
import java.util.List;

public record ReservationResponse(
        Long reservationId,
        Date reservationDate,
        Long seatCount,
        String status,
        Long transportAdId,
        float adPrice,
        Float totalPrice,
        String departureLocation,
        String destination,
        String userEmail,
        List<OptionResponse> selectedOptions
) {}