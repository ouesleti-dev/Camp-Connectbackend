package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IReviewService;
import org.example.campconnect.dto.ReviewRequestDto;
import org.example.campconnect.dto.ReviewResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    // ✅ Ajouter un review
    @PostMapping("/equipment/{equipmentId}")
    public ResponseEntity<ReviewResponseDto> addReview(
            @PathVariable Long equipmentId,
            @RequestBody ReviewRequestDto dto,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(reviewService.addReview(equipmentId, dto, email));
    }

    // ✅ Modifier un review
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto dto,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, dto, email));
    }

    // ✅ Supprimer un review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            reviewService.deleteReviewAsAdmin(reviewId);
        } else {
            reviewService.deleteReview(reviewId, email);
        }
        return ResponseEntity.noContent().build();
    }

    // ✅ Voir les reviews d'un équipement
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<ReviewResponseDto>> getByEquipment(
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(reviewService.getReviewsByEquipment(equipmentId));
    }
}