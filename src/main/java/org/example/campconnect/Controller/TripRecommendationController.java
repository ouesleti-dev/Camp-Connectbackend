package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ITripRecommendationService;
import org.example.campconnect.dto.TripRecommendationRequest;
import org.example.campconnect.dto.TripRecommendationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class TripRecommendationController {

    private final ITripRecommendationService recommendationService;

    @PostMapping("/trips")
    public ResponseEntity<List<TripRecommendationResponse>> recommendTrips(
            @RequestBody TripRecommendationRequest request) {
        return ResponseEntity.ok(recommendationService.recommendTrips(request));
    }
}
