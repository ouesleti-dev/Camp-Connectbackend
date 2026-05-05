package org.example.campconnect.dto;

import java.util.Date;
import java.util.List;

public record ReservationRequest(
        Date reservationDate,
        Long seatCount,
        String status,
        Long transportAdId,
        List<Long> optionIds
) {}