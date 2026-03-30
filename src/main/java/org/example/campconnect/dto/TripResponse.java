package org.example.campconnect.dto;

import java.util.Date;

public record TripResponse(
        Long tripId,
        String departureLocation,
        String destination,
        Date departureDate,
        float distance,
        Long vehicleId,
        String vehicleLicensePlate,
        String vehicleType
) {}
