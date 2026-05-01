package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.StoryService;
import org.example.campconnect.dto.StoryRequestDto;
import org.example.campconnect.dto.StoryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    /**
     * POST /story
     * Publier une story sur mon équipement
     * Body: { "equipmentId": 1, "promoCode": "SUMMER20", "discount": 20.0, "message": "Flash deal !" }
     */
    @PostMapping
    public ResponseEntity<StoryResponseDto> publishStory(
            @RequestBody StoryRequestDto dto,
            Authentication authentication) {

        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(storyService.publishStory(dto, ownerEmail));
    }

    /**
     * PUT /story/{storyId}
     * Modifier une story (seul le propriétaire)
     * Body: { "promoCode": "NEW10", "discount": 10.0, "message": "Updated!" }
     */
    @PutMapping("/{storyId}")
    public ResponseEntity<StoryResponseDto> updateStory(
            @PathVariable Long storyId,
            @RequestBody StoryRequestDto dto,
            Authentication authentication) {

        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(storyService.updateStory(storyId, dto, ownerEmail));
    }

    /**
     * DELETE /story/{storyId}
     * Supprimer une story (seul le propriétaire)
     */
    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> deleteStory(
            @PathVariable Long storyId,
            Authentication authentication) {

        String ownerEmail = authentication.getName();
        storyService.deleteStory(storyId, ownerEmail);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /story/{equipmentId}/apply-promo?code=SUMMER20
     * Appliquer un code promo → retourne le prix réduit
     */
    @PostMapping("/{equipmentId}/apply-promo")
    public ResponseEntity<StoryResponseDto> applyPromo(
            @PathVariable Long equipmentId,
            @RequestParam String code) {

        return ResponseEntity.ok(storyService.applyPromoCode(equipmentId, code));
    }

    /**
     * GET /story/active
     * Lister toutes les stories actives (accessible à tous)
     */
    @GetMapping("/active")
    public ResponseEntity<List<StoryResponseDto>> getActiveStories() {
        return ResponseEntity.ok(storyService.getActiveStories());
    }

    /**
     * GET /story/my
     * Mes stories actives (propriétaire connecté)
     */
    @GetMapping("/my")
    public ResponseEntity<List<StoryResponseDto>> getMyStories(Authentication authentication) {
        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(storyService.getMyStories(ownerEmail));
    }
}