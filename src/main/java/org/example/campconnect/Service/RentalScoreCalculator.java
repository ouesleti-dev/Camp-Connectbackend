package org.example.campconnect.Service;

import org.example.campconnect.Repository.RentalRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class RentalScoreCalculator {

    private final RentalRepository rentalRepository;

    // Thresholds (tune per business rules)
    private static final int HIGH_USAGE_THRESHOLD   = 15; // rentals/90 days → max score
    private static final int MEDIUM_USAGE_THRESHOLD = 7;
    private static final int ACTIVE_RENTAL_PENALTY  = 8;  // points per active rental simultaneously

    public double calculate(Long equipmentId) {
        Date ninetyDaysAgo = daysAgo(90);
        Date now = new Date();

        long recentRentals = rentalRepository.countRentalsInPeriod(equipmentId, ninetyDaysAgo);
        long activeRentals = rentalRepository.countActiveRentals(equipmentId, now);
        long totalRentals  = rentalRepository.findAllByEquipmentId(equipmentId).size();

        double usageScore = 0;

        // Rule 1: recent rental frequency
        if (recentRentals >= HIGH_USAGE_THRESHOLD) {
            usageScore += 35;
        } else if (recentRentals >= MEDIUM_USAGE_THRESHOLD) {
            usageScore += 20;
        } else if (recentRentals > 0) {
            usageScore += 10;
        }

        // Rule 2: simultaneous active rentals
        usageScore += Math.min(activeRentals * ACTIVE_RENTAL_PENALTY, 10);

        // Rule 3: overall lifetime usage bonus
        if (totalRentals > 50)      usageScore += 5;
        else if (totalRentals > 25) usageScore += 3;

        return Math.min(usageScore, 50); // cap at 50
    }

    private Date daysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }
}