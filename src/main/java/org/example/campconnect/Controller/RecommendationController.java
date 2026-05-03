package org.example.campconnect.Controller;

import org.example.campconnect.Service.RecommendationService;
import org.example.campconnect.dto.ProductResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:4200")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductResponseDTO>> getRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "4") int topN) {

        List<ProductResponseDTO> recommendations =
                recommendationService.getRecommendations(productId, topN);

        return ResponseEntity.ok(recommendations);
    }
}
