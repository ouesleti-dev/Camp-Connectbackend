package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Repository.MaintenanceRepository;
import org.example.campconnect.Service.IMaintenanceService;
import org.example.campconnect.dto.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance-scheduler")
@RequiredArgsConstructor
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;
    private final MaintenanceRepository maintenanceRepository; // ✅ ajouter

    @GetMapping("/suggest")
    public ResponseEntity<List<MaintenanceSlotDto>> suggest(
            @RequestParam Long equipmentId,
            @RequestParam(defaultValue = "3") int durationDays,
            Authentication auth) {
        String username = (auth != null) ? auth.getName() : null; // ✅
        return ResponseEntity.ok(
                maintenanceService.suggestSlots(equipmentId, durationDays, username));
    }

    @GetMapping("/impact")
    public ResponseEntity<MaintenanceImpactDto> impact(
            @RequestParam Long equipmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
            Authentication auth) {
        String username = (auth != null) ? auth.getName() : null; // ✅
        return ResponseEntity.ok(
                maintenanceService.previewImpact(equipmentId, start, end, username));
    }

    @PostMapping("/confirm")
    public ResponseEntity<MaintenanceResponseDto> confirm(
            @RequestBody MaintenanceConfirmDto dto,
            Authentication auth) {
        System.out.println(">>> AUTH: " + auth);    // ✅ log temporaire
        System.out.println(">>> DTO: " + dto);      // ✅ log temporaire
        String username = (auth != null) ? auth.getName() : null; // ✅
        return ResponseEntity.ok(
                maintenanceService.confirmMaintenance(dto, username));
    }

    @GetMapping("/history/{equipmentId}")
    public ResponseEntity<List<MaintenanceResponseDto>> history(
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(maintenanceService.getHistory(equipmentId));
    }
    @GetMapping("/dates/{equipmentId}")
    public ResponseEntity<List<Map<String, Object>>> getMaintenanceDates(
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(
                maintenanceRepository.findByEquipment_IdEquipement(equipmentId)
                        .stream()
                        .map(m -> {
                            Map<String, Object> map = new java.util.HashMap<>();
                            map.put("startDate", m.getStartdate());
                            map.put("endDate", m.getEnddate());
                            return map;
                        })
                        .collect(java.util.stream.Collectors.toList()));
    }
}