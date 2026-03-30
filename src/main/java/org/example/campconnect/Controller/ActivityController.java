package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IActivityService;
import org.example.campconnect.dto.ActivityCreateRequest;
import org.example.campconnect.dto.ActivityDTO;
import org.example.campconnect.dto.ActivityUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final IActivityService activityService;

    // ── Lecture : accessible à tous les utilisateurs authentifiés ──────────────

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAll() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    // Toutes les activités d'un événement (utile pour afficher le programme de l'event)
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ActivityDTO>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(activityService.getActivitiesByEvent(eventId));
    }

    // Toutes les activités d'un camping
    @GetMapping("/camping/{campingId}")
    public ResponseEntity<List<ActivityDTO>> getByCamping(@PathVariable Long campingId) {
        return ResponseEntity.ok(activityService.getActivitiesByCamping(campingId));
    }

    // Filtrer par difficulté (EASY / MEDIUM / HARD)
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<ActivityDTO>> getByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(activityService.getActivitiesByDifficulty(difficulty));
    }

    // ── Écriture : réservé aux CAMPOWNER et ADMIN ──────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<ActivityDTO> create(@Valid @RequestBody ActivityCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.createActivity(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<ActivityDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody ActivityUpdateRequest request) {
        return ResponseEntity.ok(activityService.updateActivity(id, request));
    }

    // ── Suppression : CAMPOWNER ou ADMIN ───────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}