package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IParticipationService;
import org.example.campconnect.dto.ParticipationCreateRequest;
import org.example.campconnect.dto.ParticipationDTO;
import org.example.campconnect.dto.ParticipationUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final IParticipationService participationService;

    // Admin voit tout
    @GetMapping
    public ResponseEntity<List<ParticipationDTO>> getAll() {
        return ResponseEntity.ok(participationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(participationService.getById(id));
    }

    // Mes participations (user connecté)
    @GetMapping("/my")
    public ResponseEntity<List<ParticipationDTO>> getMyParticipations(Authentication authentication) {
        return ResponseEntity.ok(participationService.getMyParticipations(authentication.getName()));
    }

    // Participants d'une activité
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ParticipationDTO>> getByActivity(@PathVariable Long activityId) {
        return ResponseEntity.ok(participationService.getByActivity(activityId));
    }

    // S'inscrire à une activité → user connecté automatiquement associé, anti-doublon
    @PostMapping
    public ResponseEntity<ParticipationDTO> register(@Valid @RequestBody ParticipationCreateRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participationService.register(request, authentication.getName()));
    }

    // Modifier le statut (seulement le propriétaire)
    @PutMapping("/{id}")
    public ResponseEntity<ParticipationDTO> updateStatus(@PathVariable Long id,
                                                         @Valid @RequestBody ParticipationUpdateRequest request,
                                                         Authentication authentication) {
        return ResponseEntity.ok(participationService.updateStatus(id, request, authentication.getName()));
    }

    // Annuler (seulement le propriétaire)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication authentication) {
        participationService.cancel(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ⭐ L'utilisateur annule sa propre participation
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMyParticipation(
            @PathVariable Long id,
            Authentication authentication) {
        participationService.cancelByUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}