package org.example.campconnect.Controller;

import org.example.campconnect.Service.IDemandAnalysisService;
import org.example.campconnect.dto.DemandAnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/demand-analysis")
public class DemandAnalysisController {

    private final IDemandAnalysisService demandAnalysisService;

    public DemandAnalysisController(IDemandAnalysisService demandAnalysisService) {
        this.demandAnalysisService = demandAnalysisService;
    }

    @GetMapping("/global")
    public ResponseEntity<DemandAnalysisResponse> analyzeGlobalDemand() {
        return ResponseEntity.ok(demandAnalysisService.analyzeGlobalDemand());
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity<DemandAnalysisResponse> analyzeDemandForDestination(
            @PathVariable String destination
    ) {
        return ResponseEntity.ok(demandAnalysisService.analyzeDemandForDestination(destination));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<DemandAnalysisResponse> analyzeDemandForVehicle(
            @PathVariable Long vehicleId
    ) {
        return ResponseEntity.ok(demandAnalysisService.analyzeDemandForVehicle(vehicleId));
    }
}
