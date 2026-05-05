package org.example.campconnect.Service;

import org.example.campconnect.dto.TripRecommendationRequest;
import org.example.campconnect.dto.TripRecommendationResponse;

import java.util.List;

public interface ITripRecommendationService {
    List<TripRecommendationResponse> recommendTrips(TripRecommendationRequest request);
}
