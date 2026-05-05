package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.dto.TripRecommendationRequest;
import org.example.campconnect.dto.TripRecommendationResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripRecommendationServiceImpl implements ITripRecommendationService {

    private final TripRepository tripRepository;
    private final TransportAdRepository transportAdRepository;

    @Override
    public List<TripRecommendationResponse> recommendTrips(TripRecommendationRequest request) {


        List<Trip> allTrips = tripRepository.findAll().stream()
                .filter(trip ->
                        trip.getDepartureLocation().toLowerCase()
                                .contains(request.departureLocation().toLowerCase()) &&
                                trip.getDestination().toLowerCase()
                                        .contains(request.destination().toLowerCase())
                )
                .collect(Collectors.toList());


        List<TripRecommendationResponse> recommendations = new ArrayList<>();

        for (Trip trip : allTrips) {
            List<TransportAd> ads = transportAdRepository
                    .findAll().stream()
                    .filter(ad ->
                            ad.getTrip() != null &&
                                    ad.getTrip().getTripId().equals(trip.getTripId()) &&
                                    ad.getAvailableSeats() >= request.passengerCount() &&
                                    ad.getPrice() <= request.maxPrice()
                    )
                    .collect(Collectors.toList());

            for (TransportAd ad : ads) {

                double score = calculateScore(ad, trip, request);

                recommendations.add(new TripRecommendationResponse(
                        trip.getTripId(),
                        trip.getDepartureLocation(),
                        trip.getDestination(),
                        trip.getDistance(),
                        trip.getDepartureDate() != null ? trip.getDepartureDate().toString() : "",
                        ad.getAdId(),
                        ad.getPrice(),
                        ad.getAvailableSeats(),
                        ad.getTransportType() != null ? ad.getTransportType().name() : "",
                        trip.getVehicle() != null ? trip.getVehicle().getLicensePlate() : "",
                        score
                ));
            }
        }


        recommendations.sort((a, b) -> Double.compare(b.score(), a.score()));


        return recommendations.stream().limit(10).collect(Collectors.toList());
    }

    private double calculateScore(TransportAd ad, Trip trip, TripRecommendationRequest request) {
        double score = 0.0;


        double priceScore = 0;
        if (request.maxPrice() > 0) {
            priceScore = (1.0 - (ad.getPrice() / request.maxPrice())) * 30;
        }
        score += Math.max(0, priceScore);


        double seatsScore = Math.min(ad.getAvailableSeats() / 10.0, 1.0) * 25;
        score += seatsScore;


        if (request.preferredType() != null && ad.getTransportType() != null) {
            if (ad.getTransportType().name().equalsIgnoreCase(request.preferredType())) {
                score += 20;
            }
        }

        // Criterion 4: Distance score (15 points max) — shorter = better
        double distanceScore = 0;
        if (trip.getDistance() > 0) {
            distanceScore = Math.max(0, (1.0 - (trip.getDistance() / 1000.0))) * 15;
        }
        score += distanceScore;

        // Criterion 5: Future departure date score (10 points)
        // trips departing soon but not in the past = better
        if (trip.getDepartureDate() != null) {
            long daysUntilDeparture = (trip.getDepartureDate().getTime()
                    - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            if (daysUntilDeparture >= 0 && daysUntilDeparture <= 7) {
                score += 10;
            } else if (daysUntilDeparture > 7 && daysUntilDeparture <= 30) {
                score += 5;
            }
        }

        return Math.min(100.0, score);
    }
}
