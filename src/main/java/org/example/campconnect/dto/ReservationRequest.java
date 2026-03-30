package org.example.campconnect.dto;

import java.util.Date;

public record ReservationRequest(
        Date reservationDate,
        Long seatCount,
        String status,
        Long transportAdId
) {}
