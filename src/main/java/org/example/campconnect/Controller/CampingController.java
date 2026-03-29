package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ICampingService;
import org.example.campconnect.dto.CampingCreateRequest;
import org.example.campconnect.dto.CampingDTO;
import org.example.campconnect.dto.CampingUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campings")
@RequiredArgsConstructor
public class CampingController {

    private final ICampingService campingService;

    // ── Lecture : accessible à tous les utilisateurs authentifiés ──────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CAMPOWNER','CAMPER')")
    public ResponseEntity<List<CampingDTO>> getAll() {
        return ResponseEntity.ok(campingService.getAllCampings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampingDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campingService.getCampingById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CampingDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(campingService.getCampingsByStatus(status));
    }

    // ── Écriture : réservé aux CAMPOWNER et ADMIN ──────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<CampingDTO> create(@Valid @RequestBody CampingCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campingService.createCamping(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<CampingDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody CampingUpdateRequest request) {
        return ResponseEntity.ok(campingService.updateCamping(id, request));
    }

    // ── Suppression : ADMIN uniquement ─────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campingService.deleteCamping(id);
        return ResponseEntity.noContent().build();
    }
}