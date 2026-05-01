package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Repository.ReservationRepository;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.TransportStatsResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final TransportAdRepository transportAdRepository;

    @Override
    public TransportStatsResponse getTransportStats() {
        return new TransportStatsResponse(
                reservationRepository.count(),
                vehicleRepository.count(),
                tripRepository.count(),
                transportAdRepository.count(),
                reservationRepository.sumReservedSeats(),
                reservationRepository.calculateTotalRevenue(),
                reservationRepository.countReservationsByTransportType(),
                reservationRepository.countReservationsByDestination()
        );
    }
}