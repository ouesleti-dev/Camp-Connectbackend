package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campconnect.Repository.DemandRepository;
import org.example.campconnect.dto.DemandDecisionDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandService {

    private final DemandRepository demandRepository;

    public List<DemandDecisionDto> computeAllDecisions() {
        Date now = new Date();

        // --- Date ranges ---
        Date currentStart  = daysAgo(now, 7);
        Date currentEnd    = now;
        Date previousStart = daysAgo(now, 14);
        Date previousEnd   = daysAgo(now, 7);

        // --- JPQL data fetch ---
        Map<Long, Long> currentRentals  = toMap(demandRepository.countRentalsByEquipmentInRange(currentStart, currentEnd));
        Map<Long, Long> previousRentals = toMap(demandRepository.countRentalsByEquipmentInRange(previousStart, previousEnd));
        Map<Long, Double> avgRatings    = toDoubleMap(demandRepository.avgRatingByEquipment());
        List<Object[]> equipmentList    = demandRepository.findVerifiedEquipmentBasicInfo();

        List<DemandDecisionDto> results = new ArrayList<>();

        for (Object[] row : equipmentList) {
            Long  id        = ((Number) row[0]).longValue();
            String name     = (String) row[1];
            Float basePrice = ((Number) row[2]).floatValue();

            long   current  = currentRentals.getOrDefault(id, 0L);
            long   previous = previousRentals.getOrDefault(id, 0L);
            double rating   = avgRatings.getOrDefault(id, 0.0);

            DemandDecisionDto dto = buildDecision(id, name, basePrice, current, previous, rating);
            results.add(dto);
        }

        // Sort by demandScore descending
        results.sort(Comparator.comparingDouble(DemandDecisionDto::getDemandScore).reversed());
        return results;
    }

    // ─────────────────────────────────────────────────────────────
    // Core decision engine
    // ─────────────────────────────────────────────────────────────
    private DemandDecisionDto buildDecision(Long id, String name, float basePrice,
                                            long current, long previous, double rating) {

        // A. Trend
        double trend;
        if (previous == 0) {
            trend = current > 0 ? 1.0 : 0.0; // safe division by zero
        } else {
            trend = (double)(current - previous) / previous;
        }

        // B. Rating Score (normalize 0–5 → 0–1)
        double ratingScore = rating / 5.0;

        // C. Demand Score
        double normalizedRentals = Math.min(current / 10.0, 1.0); // cap at 10 rentals
        double demandScore = 0.5 * normalizedRentals
                + 0.3 * ratingScore
                + 0.2 * Math.max(-1.0, Math.min(trend, 1.0)); // clamp trend

        // D. Prediction
        String prediction;
        if      (trend >  0.2) prediction = "GROWING";
        else if (trend < -0.2) prediction = "DECLINING";
        else                   prediction = "STABLE";

        // E. Pricing Action
        String action;
        if      (trend > 0.3 && ratingScore > 0.7)           action = "INCREASE_PRICE";
        else if (trend >= 0 && trend <= 0.3)                  action = "SLIGHT_INCREASE";
        else if (trend < -0.2)                                action = "DECREASE_PRICE";
        else                                                   action = "KEEP_PRICE";

        // F. Price Adjustment
        double adjustment = switch (action) {
            case "INCREASE_PRICE"  -> 0.15;
            case "SLIGHT_INCREASE" -> 0.05;
            case "DECREASE_PRICE"  -> -0.10;
            default                -> 0.0;
        };
        double suggestedPrice = basePrice * (1 + adjustment);

        // Human-readable explanations
        String trendExplanation = switch (prediction) {
            case "GROWING"   -> "Demand is increasing — rentals up " + String.format("%.0f%%", trend * 100);
            case "DECLINING" -> "Demand is declining — rentals down " + String.format("%.0f%%", Math.abs(trend) * 100);
            default          -> "Demand is stable over the past 14 days";
        };

        String priceRecommendation = switch (action) {
            case "INCREASE_PRICE"  -> "Increase price by 15% (high demand + great ratings)";
            case "SLIGHT_INCREASE" -> "Slight increase of 5% (steady demand)";
            case "DECREASE_PRICE"  -> "Decrease price by 10% (boost low demand)";
            default                -> "Keep current price (balanced market)";
        };

        return DemandDecisionDto.builder()
                .equipmentId(id)
                .equipmentName(name)
                .basePrice(basePrice)
                .currentRentals(current)
                .previousRentals(previous)
                .averageRating(Math.round(rating * 10.0) / 10.0)
                .trend(Math.round(trend * 1000.0) / 1000.0)
                .ratingScore(Math.round(ratingScore * 1000.0) / 1000.0)
                .demandScore(Math.round(demandScore * 1000.0) / 1000.0)
                .prediction(prediction)
                .action(action)
                .suggestedPrice(Math.round(suggestedPrice * 100.0) / 100.0)
                .trendExplanation(trendExplanation)
                .priceRecommendation(priceRecommendation)
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────
    private Date daysAgo(Date from, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }

    private Map<Long, Long> toMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                r -> ((Number) r[0]).longValue(),
                r -> ((Number) r[1]).longValue()
        ));
    }

    private Map<Long, Double> toDoubleMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                r -> ((Number) r[0]).longValue(),
                r -> ((Number) r[1]).doubleValue()
        ));
    }
}