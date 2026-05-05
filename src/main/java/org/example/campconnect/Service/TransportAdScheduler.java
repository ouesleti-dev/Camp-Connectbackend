package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Repository.TransportAdRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransportAdScheduler {

    private final TransportAdRepository transportAdRepository;

    @Transactional
    @Scheduled(fixedDelayString = "${transport.scheduler.expired-trip-seats-ms:60000}")
    public void closeExpiredTripAdsSeats() {
        Date now = new Date();
        List<TransportAd> expiredAds = transportAdRepository
                .findByTripDepartureDateBeforeAndAvailableSeatsGreaterThan(now, 0L);

        if (expiredAds.isEmpty()) {
            return;
        }

        // Past trips should not keep bookable seats.
        expiredAds.forEach(ad -> ad.setAvailableSeats(0L));
        transportAdRepository.saveAll(expiredAds);
    }
}
