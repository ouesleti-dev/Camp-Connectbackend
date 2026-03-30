package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.TransportType;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.dto.TransportAdRequest;
import org.example.campconnect.dto.TransportAdResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportAdServiceImpl implements ITransportAdService {

    private final TransportAdRepository transportAdRepository;
    private final TripRepository tripRepository;

    private TransportAdResponse toResponse(TransportAd ad) {
        Trip trip = ad.getTrip();
        return new TransportAdResponse(
                ad.getAdId(),
                ad.getPrice(),
                ad.getAvailableSeats(),
                ad.getTransportType() != null ? ad.getTransportType().name() : null,
                trip != null ? trip.getTripId() : null,
                trip != null ? trip.getDepartureLocation() : null,
                trip != null ? trip.getDestination() : null,
                trip != null && trip.getVehicle() != null ? trip.getVehicle().getLicensePlate() : null
        );
    }

    private void validateRequest(TransportAdRequest req) {
        if (req.price() <= 0) {
            throw new IllegalArgumentException("Le prix doit etre positif");
        }
        if (req.availableSeats() == null || req.availableSeats() <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit etre positif");
        }
        if (req.transportType() == null || req.transportType().isBlank()) {
            throw new IllegalArgumentException("Le type de transport est requis");
        }
        if (req.tripId() == null) {
            throw new IllegalArgumentException("Le tripId est requis");
        }
    }

    private TransportType parseTransportType(String type) {
        try {
            return TransportType.valueOf(type.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Type de transport invalide. Valeurs autorisees: " + Arrays.toString(TransportType.values())
            );
        }
    }

    private Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip introuvable avec l'id : " + tripId));
    }

    @Override
    @Transactional
    public TransportAdResponse createTransportAd(TransportAdRequest req) {
        validateRequest(req);
        Trip trip = getTripById(req.tripId());
        TransportType transportType = parseTransportType(req.transportType());

        TransportAd ad = new TransportAd();
        ad.setPrice(req.price());
        ad.setAvailableSeats(req.availableSeats());
        ad.setTransportType(transportType);
        ad.setTrip(trip);

        return toResponse(transportAdRepository.save(ad));
    }

    @Override
    @Transactional
    public TransportAdResponse updateTransportAd(Long id, TransportAdRequest req) {
        validateRequest(req);
        Trip trip = getTripById(req.tripId());
        TransportType transportType = parseTransportType(req.transportType());

        TransportAd existing = transportAdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TransportAd introuvable avec l'id : " + id));

        existing.setPrice(req.price());
        existing.setAvailableSeats(req.availableSeats());
        existing.setTransportType(transportType);
        existing.setTrip(trip);

        return toResponse(transportAdRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteTransportAd(Long id) {
        TransportAd ad = transportAdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable : " + id));

        if (ad.getTrip() != null) {
            Trip trip = ad.getTrip();
            trip.setTransportAd(null);
            tripRepository.save(trip);
            ad.setTrip(null);
            transportAdRepository.save(ad);
        }

        transportAdRepository.delete(ad);
    }

    @Override
    public TransportAdResponse getTransportAdById(Long id) {
        return transportAdRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("TransportAd introuvable avec l'id : " + id));
    }

    @Override
    public List<TransportAdResponse> getAllTransportAds() {
        return transportAdRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TransportAdResponse> getByTransportType(String type) {
        TransportType transportType = parseTransportType(type);
        return transportAdRepository.findByTransportType(transportType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TransportAdResponse> getByAvailableSeats(Long minSeats) {
        if (minSeats == null || minSeats <= 0) {
            throw new IllegalArgumentException("Le nombre minimum de places doit etre positif");
        }

        return transportAdRepository.findByAvailableSeatsGreaterThanEqual(minSeats)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TransportAdResponse> getByMaxPrice(float maxPrice) {
        if (maxPrice <= 0) {
            throw new IllegalArgumentException("Le prix maximum doit etre positif");
        }

        return transportAdRepository.findByPriceLessThanEqual(maxPrice)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TransportAdResponse> getByTripId(Long tripId) {
        return transportAdRepository.findByTripTripId(tripId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
