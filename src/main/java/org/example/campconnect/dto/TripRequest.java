package org.example.campconnect.dto;

import java.util.Date;

public record TripRequest(
        String departureLocation,
        String destination,
        Date departureDate,
        float distance,
        Long vehicleId
) {}
