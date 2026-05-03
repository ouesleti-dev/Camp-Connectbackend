package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IProductReviewService;
import org.example.campconnect.dto.ProductReviewRequest;
import org.example.campconnect.dto.ProductReviewResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-reviews")
@RequiredArgsConstructor
public class  ProductReviewController {

    private final IProductReviewService reviewService;

    @PostMapping
    public ResponseEntity<ProductReviewResponseDTO> addReview(@Valid @RequestBody ProductReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReviewResponseDTO>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> hasReviewed(@RequestParam Long userId, @RequestParam Long productId) {
        return ResponseEntity.ok(reviewService.hasUserReviewedProduct(userId, productId));
    }
}