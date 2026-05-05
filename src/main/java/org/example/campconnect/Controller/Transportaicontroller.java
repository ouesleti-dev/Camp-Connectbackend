package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/transport-ai")
@RequiredArgsConstructor
public class Transportaicontroller {

    private final RestTemplate restTemplate;
    private final String AI_URL = "http://localhost:5001";

    // ─── POST /transport-ai/predict-price ────────────────────────────────
    // Predict price by distance (simple)
    @PostMapping("/predict-price")
    public ResponseEntity<Map<String, Object>> predictByDistance(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build request for Python API
        Map<String, Object> aiRequest = new HashMap<>();
        aiRequest.put("distance_km",      request.get("distance_km"));
        aiRequest.put("passenger_count",  request.getOrDefault("passenger_count", 1));
        aiRequest.put("hour",             request.getOrDefault("hour", 12));
        aiRequest.put("weekday",          request.getOrDefault("weekday", 1));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(aiRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                AI_URL + "/predict-by-distance",
                entity,
                Map.class
        );

        return ResponseEntity.ok(response.getBody());
    }

    // ─── POST /transport-ai/predict-by-coordinates ───────────────────────
    // Predict price using GPS coordinates
    @PostMapping("/predict-by-coordinates")
    public ResponseEntity<Map<String, Object>> predictByCoordinates(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                AI_URL + "/predict-by-coordinates",
                entity,
                Map.class
        );

        return ResponseEntity.ok(response.getBody());
    }

    // ─── GET /transport-ai/health ─────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    AI_URL + "/health", Map.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "offline",
                    "message", "AI server not running on port 5001"
            ));
        }
    }
}