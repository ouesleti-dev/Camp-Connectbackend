package org.example.campconnect.dto;

public record TripRecommendationRequest(
        String departureLocation,
        String destination,
        int passengerCount,
        float maxPrice,
        String preferredType
) {
}
