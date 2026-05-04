package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IStatsService;
import org.example.campconnect.dto.CampingRankingDTO;
import org.example.campconnect.dto.EventStatsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final IStatsService statsService;

    // ⭐ Stats complètes d'un event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventStatsDTO> getEventStats(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(statsService.getEventStats(eventId));
    }

    // ⭐ Classement des campings les plus actifs
    @GetMapping("/campings/ranking")
    public ResponseEntity<List<CampingRankingDTO>> getCampingRanking() {
        return ResponseEntity.ok(statsService.getCampingRanking());
    }

    // ⭐ Export données pour entraînement ML dropout
    @GetMapping("/dropout-features")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getDropoutFeatures() {
        return ResponseEntity.ok(statsService.getDropoutFeatures());
    }
}