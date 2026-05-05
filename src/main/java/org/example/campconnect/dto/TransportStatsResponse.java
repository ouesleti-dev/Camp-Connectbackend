package org.example.campconnect.dto;

import java.util.List;

public record TransportStatsResponse(
        Long totalReservations,
        Long totalVehicles,
        Long totalTrips,
        Long totalAds,
        Long totalReservedSeats,
        Double totalRevenue,
        List<GroupedStatResponse> reservationsByTransportType,
        List<GroupedStatResponse> reservationsByDestination
) {
}