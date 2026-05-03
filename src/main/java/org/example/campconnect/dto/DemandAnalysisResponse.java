package org.example.campconnect.dto;

import java.util.List;
import java.util.Map;

public record DemandAnalysisResponse(
        Long totalReservations,
        Long totalTrips,
        Double averageOccupancyRate,
        Map<String, Long> reservationsByHour,
        String peakHour,
        String lowHour,
        Map<String, Long> reservationsByDayOfWeek,
        String peakDay,
        List<GroupedStatResponse> topDestinations,
        DemandLevel currentDemandLevel,
        String demandAdvice,
        Double demandScore
) {
    public enum DemandLevel {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }
}
