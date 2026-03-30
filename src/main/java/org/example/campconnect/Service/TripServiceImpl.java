package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.TripRequest;
import org.example.campconnect.dto.TripResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements ITripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    private TripResponse toResponse(Trip trip) {
        Vehicle vehicle = trip.getVehicle();
        return new TripResponse(
                trip.getTripId(),
                trip.getDepartureLocation(),
                trip.getDestination(),
                trip.getDepartureDate(),
                trip.getDistance(),
                vehicle != null ? vehicle.getVehicleId() : null,
                vehicle != null ? vehicle.getLicensePlate() : null,
                vehicle != null ? vehicle.getVehicleType() : null
        );
    }

    private void validateRequest(TripRequest req) {
        if (req.departureLocation() == null || req.departureLocation().isBlank()) {
            throw new IllegalArgumentException("Le lieu de depart est requis");
        }
        if (req.destination() == null || req.destination().isBlank()) {
            throw new IllegalArgumentException("La destination est requise");
        }
        if (req.departureDate() == null) {
            throw new IllegalArgumentException("La date de depart est requise");
        }
        if (req.distance() <= 0) {
            throw new IllegalArgumentException("La distance doit etre positive");
        }
        if (req.vehicleId() == null) {
            throw new IllegalArgumentException("Le vehicleId est requis");
        }
    }

    private Vehicle getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable avec l'id : " + vehicleId));
    }

    @Override
    @Transactional
    public TripResponse createTrip(TripRequest req) {
        validateRequest(req);
        Vehicle vehicle = getVehicleById(req.vehicleId());

        Trip trip = new Trip();
        trip.setDepartureLocation(req.departureLocation().trim());
        trip.setDestination(req.destination().trim());
        trip.setDepartureDate(req.departureDate());
        trip.setDistance(req.distance());
        trip.setVehicle(vehicle);

        return toResponse(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripResponse updateTrip(Long id, TripRequest req) {
        validateRequest(req);
        Vehicle vehicle = getVehicleById(req.vehicleId());

        Trip existing = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip introuvable avec l'id : " + id));

        existing.setDepartureLocation(req.departureLocation().trim());
        existing.setDestination(req.destination().trim());
        existing.setDepartureDate(req.departureDate());
        existing.setDistance(req.distance());
        existing.setVehicle(vehicle);

        return toResponse(tripRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteTrip(Long id) {
        Trip existing = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip introuvable avec l'id : " + id));
        tripRepository.deleteById(existing.getTripId());
    }

    @Override
    public TripResponse getTripById(Long id) {
        return tripRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Trip introuvable avec l'id : " + id));
    }

    @Override
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TripResponse> getTripsByVehicleId(Long vehicleId) {
        return tripRepository.findByVehicleVehicleId(vehicleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TripResponse> getTripsByDestination(String destination) {
        return tripRepository.findByDestination(destination)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TripResponse> getTripsByDeparture(String departureLocation) {
        return tripRepository.findByDepartureLocation(departureLocation)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TripResponse> getUpcomingTrips(Date fromDate) {
        if (fromDate == null) {
            throw new IllegalArgumentException("La date de reference est requise");
        }

        return tripRepository.findByDepartureDateAfter(fromDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
