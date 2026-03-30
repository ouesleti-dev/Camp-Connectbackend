package org.example.campconnect.dto;

public record TransportAdResponse(
        Long adId,
        float price,
        Long availableSeats,
        String transportType,
        Long tripId,
        String departureLocation,
        String destination,
        String vehicleLicensePlate
) {}
