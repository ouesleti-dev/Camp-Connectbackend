package org.example.campconnect.dto;

public record TripRecommendationResponse(
        Long tripId,
        String departureLocation,
        String destination,
        float distance,
        String departureDate,
        Long adId,
        float price,
        Long availableSeats,
        String transportType,
        String vehicleLicensePlate,
        double score
) {
}
