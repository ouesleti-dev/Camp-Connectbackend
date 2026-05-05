package org.example.campconnect.dto;

import java.util.List;

public record MyTransportAdDetailsResponse(
        Long adId,
        Float price,
        Long availableSeats,
        String transportType,
        String departureLocation,
        String destination,
        Long vehicleId,
        String vehicleLicensePlate,
        String vehicleType,
        List<AdReservationUserDetailResponse> reservations
) {}
