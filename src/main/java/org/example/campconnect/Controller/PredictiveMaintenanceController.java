package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.dto.MaintenancePredictionDTO;
import org.example.campconnect.Service.PredictiveMaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PredictiveMaintenanceController {

    private final PredictiveMaintenanceService predictiveMaintenanceService;

    @GetMapping("/predict")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MaintenancePredictionDTO>> predict() {
        return ResponseEntity.ok(predictiveMaintenanceService.predictForCurrentUser());
    }
}