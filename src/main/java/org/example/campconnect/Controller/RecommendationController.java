package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.RecommendationService;
import org.example.campconnect.dto.RecommendationRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<Map<?, ?>> recommend(@RequestBody RecommendationRequestDto dto) {
        return ResponseEntity.ok(recommendationService.recommend(dto));
    }
}
