package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.RecommendationService;
import org.example.campconnect.dto.ProductResponseDTO;
import org.example.campconnect.dto.RecommendationRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // GET /api/recommendations/{productId}?topN=4
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductResponseDTO>> getRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "4") int topN) {

        List<ProductResponseDTO> recommendations =
                recommendationService.getRecommendations(productId, topN);
        return ResponseEntity.ok(recommendations);
    }

    // POST /api/recommendations
    @PostMapping
    public ResponseEntity<Map<?, ?>> recommend(@RequestBody RecommendationRequestDto dto) {
        return ResponseEntity.ok(recommendationService.recommend(dto));
    }
}