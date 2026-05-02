package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.DemandService;
import org.example.campconnect.dto.DemandDecisionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demand")
@RequiredArgsConstructor
public class Demandcontroller {
    private final DemandService demandService;

    // GET /demand/decisions → all equipment decisions
    @GetMapping("/decisions")
    public ResponseEntity<List<DemandDecisionDto>> getAllDecisions() {
        return ResponseEntity.ok(demandService.computeAllDecisions());
    }
}
