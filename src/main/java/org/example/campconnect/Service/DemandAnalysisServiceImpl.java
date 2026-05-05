package org.example.campconnect.Service;

import org.example.campconnect.Entity.Reservation;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.ReservationRepository;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.dto.DemandAnalysisResponse;
import org.example.campconnect.dto.GroupedStatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DemandAnalysisServiceImpl implements IDemandAnalysisService {

    private static final List<DayOfWeek> ORDERED_DAYS = List.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
    );

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;

    public DemandAnalysisServiceImpl(
            ReservationRepository reservationRepository,
            TripRepository tripRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.tripRepository = tripRepository;
    }

    @Override
    public DemandAnalysisResponse analyzeGlobalDemand() {
        return buildAnalysis(
                reservationRepository.findAll(),
                tripRepository.findAll()
        );
    }

    @Override
    public DemandAnalysisResponse analyzeDemandForDestination(String destination) {
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination requise");
        }

        String normalizedDestination = destination.trim();
        List<Trip> trips = tripRepository.findAll()
                .stream()
                .filter(trip -> matchesDestination(trip, normalizedDestination))
                .toList();

        List<Reservation> reservations = reservationRepository.findAll()
                .stream()
                .filter(reservation -> matchesDestination(reservation, normalizedDestination))
                .toList();

        return buildAnalysis(reservations, trips);
    }

    @Override
    public DemandAnalysisResponse analyzeDemandForVehicle(Long vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId requis");
        }

        List<Trip> trips = tripRepository.findAll()
                .stream()
                .filter(trip -> matchesVehicle(trip, vehicleId))
                .toList();

        Set<Long> tripIds = trips.stream()
                .map(Trip::getTripId)
                .collect(Collectors.toSet());

        List<Reservation> reservations = reservationRepository.findAll()
                .stream()
                .filter(reservation -> {
                    Long tripId = tripIdOf(reservation);
                    return tripId != null && tripIds.contains(tripId);
                })
                .toList();

        return buildAnalysis(reservations, trips);
    }

    private DemandAnalysisResponse buildAnalysis(List<Reservation> reservations, List<Trip> trips) {
        long totalReservations = reservations.size();
        long totalTrips = trips.size();
        double averageOccupancyRate = calculateAverageOccupancyRate(reservations, trips);
        Map<String, Long> reservationsByHour = calculateReservationsByHour(reservations);
        Map<String, Long> reservationsByDayOfWeek = calculateReservationsByDayOfWeek(reservations);
        List<GroupedStatResponse> topDestinations = calculateTopDestinations(reservations);
        double demandScore = calculateDemandScore(totalReservations, averageOccupancyRate, totalTrips);
        DemandAnalysisResponse.DemandLevel demandLevel = determineDemandLevel(demandScore);

        return new DemandAnalysisResponse(
                totalReservations,
                totalTrips,
                round(averageOccupancyRate),
                reservationsByHour,
                findMaxKey(reservationsByHour),
                findMinKey(reservationsByHour),
                reservationsByDayOfWeek,
                findMaxKey(reservationsByDayOfWeek),
                topDestinations,
                demandLevel,
                buildDemandAdvice(demandLevel),
                round(demandScore)
        );
    }

    private Map<String, Long> calculateReservationsByHour(List<Reservation> reservations) {
        Map<String, Long> reservationsByHour = new LinkedHashMap<>();
        for (int hour = 0; hour < 24; hour++) {
            reservationsByHour.put(formatHour(hour), 0L);
        }

        reservations.stream()
                .map(Reservation::getReservationDate)
                .filter(date -> date != null)
                .map(this::toLocalDateTime)
                .map(LocalDateTime::getHour)
                .map(this::formatHour)
                .forEach(hour -> reservationsByHour.merge(hour, 1L, Long::sum));

        return reservationsByHour;
    }

    private Map<String, Long> calculateReservationsByDayOfWeek(List<Reservation> reservations) {
        Map<String, Long> reservationsByDay = new LinkedHashMap<>();
        ORDERED_DAYS.forEach(day -> reservationsByDay.put(formatDay(day), 0L));

        reservations.stream()
                .map(Reservation::getReservationDate)
                .filter(date -> date != null)
                .map(this::toLocalDateTime)
                .map(LocalDateTime::getDayOfWeek)
                .map(this::formatDay)
                .forEach(day -> reservationsByDay.merge(day, 1L, Long::sum));

        return reservationsByDay;
    }

    private List<GroupedStatResponse> calculateTopDestinations(List<Reservation> reservations) {
        Map<String, Long> groupedDestinations = new HashMap<>();

        reservations.stream()
                .map(this::destinationOf)
                .filter(destination -> destination != null && !destination.isBlank())
                .map(String::trim)
                .forEach(destination -> groupedDestinations.merge(destination, 1L, Long::sum));

        return groupedDestinations.entrySet()
                .stream()
                .sorted((first, second) -> {
                    int valueComparison = Long.compare(second.getValue(), first.getValue());
                    if (valueComparison != 0) {
                        return valueComparison;
                    }
                    return first.getKey().compareToIgnoreCase(second.getKey());
                })
                .limit(5)
                .map(entry -> new GroupedStatResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private double calculateAverageOccupancyRate(List<Reservation> reservations, List<Trip> trips) {
        Map<Long, Long> reservedSeatsByTrip = calculateReservedSeatsByTrip(reservations);
        List<Double> occupancyRates = new ArrayList<>();

        for (Trip trip : trips) {
            if (trip == null || trip.getTripId() == null) {
                continue;
            }

            long reservedSeats = reservedSeatsByTrip.getOrDefault(trip.getTripId(), 0L);
            Long capacity = capacityOf(trip, reservedSeats);

            if (capacity != null && capacity > 0) {
                double occupancyRate = (reservedSeats * 100.0) / capacity;
                occupancyRates.add(Math.min(occupancyRate, 100.0));
            }
        }

        if (occupancyRates.isEmpty()) {
            return 0.0;
        }

        return occupancyRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Map<Long, Long> calculateReservedSeatsByTrip(List<Reservation> reservations) {
        Map<Long, Long> reservedSeatsByTrip = new HashMap<>();

        for (Reservation reservation : reservations) {
            Long tripId = tripIdOf(reservation);
            if (tripId != null) {
                reservedSeatsByTrip.merge(tripId, seatCountOf(reservation), Long::sum);
            }
        }

        return reservedSeatsByTrip;
    }

    private double calculateDemandScore(long totalReservations, double occupancyRate, long totalTrips) {
        double reservationScore = Math.min(totalReservations * 5.0, 40.0);
        double occupancyScore = Math.min(occupancyRate * 0.4, 40.0);
        double tripScore = Math.min(totalTrips * 2.0, 20.0);

        return Math.min(reservationScore + occupancyScore + tripScore, 100.0);
    }

    private DemandAnalysisResponse.DemandLevel determineDemandLevel(double demandScore) {
        if (demandScore <= 25) {
            return DemandAnalysisResponse.DemandLevel.LOW;
        }

        if (demandScore <= 50) {
            return DemandAnalysisResponse.DemandLevel.MEDIUM;
        }

        if (demandScore <= 75) {
            return DemandAnalysisResponse.DemandLevel.HIGH;
        }

        return DemandAnalysisResponse.DemandLevel.VERY_HIGH;
    }

    private String buildDemandAdvice(DemandAnalysisResponse.DemandLevel demandLevel) {
        return switch (demandLevel) {
            case VERY_HIGH -> "Demande tres elevee : augmenter les prix et recommander aux passagers de reserver vite.";
            case HIGH -> "Demande forte : bon moment pour proposer plus de trajets ou optimiser les disponibilites.";
            case MEDIUM -> "Demande normale : maintenir les prix et surveiller les prochaines reservations.";
            case LOW -> "Demande faible : reduire les prix ou proposer des offres pour stimuler les reservations.";
        };
    }

    private Long capacityOf(Trip trip, long reservedSeats) {
        Vehicle vehicle = trip.getVehicle();
        if (vehicle != null && vehicle.getCapacity() != null && vehicle.getCapacity() > 0) {
            return vehicle.getCapacity();
        }

        TransportAd transportAd = trip.getTransportAd();
        if (transportAd != null && transportAd.getAvailableSeats() != null) {
            long estimatedCapacity = reservedSeats + Math.max(transportAd.getAvailableSeats(), 0L);
            if (estimatedCapacity > 0) {
                return estimatedCapacity;
            }
        }

        return null;
    }

    private boolean matchesDestination(Trip trip, String destination) {
        return trip != null
                && trip.getDestination() != null
                && trip.getDestination().trim().equalsIgnoreCase(destination);
    }

    private boolean matchesDestination(Reservation reservation, String destination) {
        String reservationDestination = destinationOf(reservation);
        return reservationDestination != null
                && reservationDestination.trim().equalsIgnoreCase(destination);
    }

    private boolean matchesVehicle(Trip trip, Long vehicleId) {
        return trip != null
                && trip.getVehicle() != null
                && trip.getVehicle().getVehicleId() != null
                && trip.getVehicle().getVehicleId().equals(vehicleId);
    }

    private String destinationOf(Reservation reservation) {
        if (reservation == null
                || reservation.getTransportAd() == null
                || reservation.getTransportAd().getTrip() == null) {
            return null;
        }

        return reservation.getTransportAd().getTrip().getDestination();
    }

    private Long tripIdOf(Reservation reservation) {
        if (reservation == null
                || reservation.getTransportAd() == null
                || reservation.getTransportAd().getTrip() == null) {
            return null;
        }

        return reservation.getTransportAd().getTrip().getTripId();
    }

    private long seatCountOf(Reservation reservation) {
        return reservation != null && reservation.getSeatCount() != null
                ? reservation.getSeatCount()
                : 0L;
    }

    private LocalDateTime toLocalDateTime(java.util.Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private String formatHour(int hour) {
        return String.format("%02dh", hour);
    }

    private String formatDay(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private String findMaxKey(Map<String, Long> values) {
        String maxKey = null;
        long maxValue = Long.MIN_VALUE;

        for (Map.Entry<String, Long> entry : values.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }

        return maxKey;
    }

    private String findMinKey(Map<String, Long> values) {
        String minKey = null;
        long minValue = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : values.entrySet()) {
            if (entry.getValue() < minValue) {
                minValue = entry.getValue();
                minKey = entry.getKey();
            }
        }

        return minKey;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
