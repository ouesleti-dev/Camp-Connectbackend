package org.example.campconnect.dto;

public record TransportAdRequest(
        float price,
        Long availableSeats,
        String transportType,
        Long tripId
) {}
