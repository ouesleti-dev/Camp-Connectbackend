package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.AdminDashboardService;
import org.example.campconnect.dto.TransportStatsResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/transport-stats")
    public TransportStatsResponse getTransportStats() {
        return adminDashboardService.getTransportStats();
    }
}