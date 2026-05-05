package org.example.campconnect.Scheduler;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IPatnershipService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartnershipScheduler {

    private final IPatnershipService service;

    // ⏱ Tous les jours à minuit
    @Scheduled(cron = "0 0 0 * * *")
    public void run() {
        service.runPartnershipScheduler();
    }
}