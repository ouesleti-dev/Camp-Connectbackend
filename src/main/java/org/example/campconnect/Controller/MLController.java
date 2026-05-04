package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IMLService;
import org.example.campconnect.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ml")
@RequiredArgsConstructor
public class MLController {

    private final IMLService mlService;

    // ⭐ Proxy dropout — Spring vérifie JWT + rôle avant d'appeler FastAPI
    @PostMapping("/predict-dropout")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<DropoutPredictionDTO> predictDropout(
            @Valid @RequestBody DropoutFeaturesDTO features) {
        return ResponseEntity.ok(mlService.predictDropout(features));
    }

    // ⭐ Proxy participation — même protection
    @PostMapping("/predict-participation")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<EventPredictionDTO> predictParticipation(
            @Valid @RequestBody EventFeaturesDTO features) {
        return ResponseEntity.ok(mlService.predictParticipation(features));
    }
}