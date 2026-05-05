package org.example.campconnect.dto;

import java.util.Date;
import java.util.List;

public record AdReservationUserDetailResponse(
        Long reservationId,
        String userEmail,
        String userPhone,
        Long seatCount,
        Float totalPrice,
        String status,
        Date reservationDate,
        List<OptionResponse> selectedOptions
) {}
