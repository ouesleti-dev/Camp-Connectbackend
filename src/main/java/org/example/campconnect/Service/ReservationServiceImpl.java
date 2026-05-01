package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Reservation;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.TransportType;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.ReservationRepository;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ReservationDetailsResponse;
import org.example.campconnect.dto.ReservationRequest;
import org.example.campconnect.dto.ReservationResponse;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final TransportAdRepository transportAdRepository;
    private final UserRepository userRepository;

    // ========================= CONVERTERS =========================

    private ReservationResponse toResponse(Reservation reservation, String userEmail) {

        TransportAd ad = reservation.getTransportAd();
        Trip trip = ad != null ? ad.getTrip() : null;

        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getReservationDate(),
                reservation.getSeatCount(),
                reservation.getStatus(),
                ad != null ? ad.getAdId() : null,
                ad != null ? ad.getPrice() : 0,
                trip != null ? trip.getDepartureLocation() : null,
                trip != null ? trip.getDestination() : null,
                userEmail
        );
    }

    private ReservationDetailsResponse toDetailsResponse(Reservation reservation) {
        TransportAd ad = reservation.getTransportAd();
        Trip trip = ad != null ? ad.getTrip() : null;

        return new ReservationDetailsResponse(
                reservation.getReservationId(),
                reservation.getReservationDate(),
                reservation.getSeatCount(),
                reservation.getStatus(),
                ad != null ? ad.getPrice() : 0,
                ad != null ? ad.getTransportType() : null,
                trip != null ? trip.getDestination() : null,
                trip != null && trip.getVehicle() != null
                        ? trip.getVehicle().getLicensePlate()
                        : null
        );
    }

    // ========================= VALIDATION =========================

    private void validateRequest(ReservationRequest req) {

        if (req == null) {
            throw new IllegalArgumentException("La requête est vide");
        }

        if (req.reservationDate() == null) {
            throw new IllegalArgumentException("La date de réservation est requise");
        }

        if (req.seatCount() == null || req.seatCount() <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit être positif");
        }

        if (req.transportAdId() == null) {
            throw new IllegalArgumentException("Le transportAdId est requis");
        }
    }

    private TransportAd getTransportAdById(Long id) {
        return transportAdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private TransportType parseTransportType(String transportType) {

        if (transportType == null || transportType.isBlank()) {
            throw new IllegalArgumentException("Le type transport est requis");
        }

        try {
            return TransportType.valueOf(transportType.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Type invalide. Valeurs possibles : "
                            + Arrays.toString(TransportType.values())
            );
        }
    }

    // ========================= CREATE =========================

    @Override
    public ReservationResponse createReservation(ReservationRequest req, String userEmail) {

        validateRequest(req);

        TransportAd ad = getTransportAdById(req.transportAdId());

        if (ad.getAvailableSeats() < req.seatCount()) {
            throw new IllegalArgumentException("Nombre de places insuffisant");
        }

        User user = getUserByEmail(userEmail);

        Reservation reservation = new Reservation();
        reservation.setReservationDate(req.reservationDate());
        reservation.setSeatCount(req.seatCount());
        reservation.setStatus(req.status());
        reservation.setTransportAd(ad);

        Reservation saved = reservationRepository.save(reservation);

        ad.setAvailableSeats(ad.getAvailableSeats() - req.seatCount());
        transportAdRepository.save(ad);

        if (user.getReservations() == null) {
            user.setReservations(new ArrayList<>());
        }

        user.getReservations().add(saved);
        userRepository.save(user);

        return toResponse(saved, userEmail);
    }

    // ========================= UPDATE =========================

    @Override
    public ReservationResponse updateReservation(
            Long id,
            ReservationRequest req,
            String userEmail
    ) {

        validateRequest(req);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        TransportAd oldAd = reservation.getTransportAd();
        TransportAd newAd = getTransportAdById(req.transportAdId());

        if (oldAd != null) {
            oldAd.setAvailableSeats(oldAd.getAvailableSeats() + reservation.getSeatCount());
            transportAdRepository.save(oldAd);
        }

        if (newAd.getAvailableSeats() < req.seatCount()) {
            throw new IllegalArgumentException("Places insuffisantes");
        }

        newAd.setAvailableSeats(newAd.getAvailableSeats() - req.seatCount());
        transportAdRepository.save(newAd);

        reservation.setReservationDate(req.reservationDate());
        reservation.setSeatCount(req.seatCount());
        reservation.setStatus(req.status());
        reservation.setTransportAd(newAd);

        Reservation updated = reservationRepository.save(reservation);

        return toResponse(updated, userEmail);
    }

// ========================= DELETE =========================

    @Override
    public void deleteReservation(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        TransportAd ad = reservation.getTransportAd();

        if (ad != null) {

            // remettre les places
            ad.setAvailableSeats(ad.getAvailableSeats() + reservation.getSeatCount());

            // casser la relation OneToOne
            ad.setReservation(null);

            transportAdRepository.save(ad);
        }

        reservationRepository.delete(reservation);
    }

    // ========================= READ =========================

    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        return toResponse(reservation, null);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(r -> toResponse(r, null))
                .toList();
    }

    @Override
    public List<ReservationResponse> getByStatus(String status) {

        return reservationRepository.findByStatus(status)
                .stream()
                .map(r -> toResponse(r, null))
                .toList();
    }

    @Override
    public List<ReservationResponse> getByUserEmail(String email) {

        return reservationRepository.findByUserEmail(email)
                .stream()
                .map(r -> toResponse(r, email))
                .toList();
    }

    @Override
    public List<ReservationResponse> getByTransportAdId(Long transportAdId) {

        return reservationRepository.findByTransportAdAdId(transportAdId)
                .stream()
                .map(r -> toResponse(r, null))
                .toList();
    }

    @Override
    public List<ReservationDetailsResponse> getDetailedReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(this::toDetailsResponse)
                .toList();
    }

    @Override
    public List<ReservationDetailsResponse> searchByDestinationAndTransportType(
            String destination,
            String transportType
    ) {

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination requise");
        }

        TransportType parsedType = parseTransportType(transportType);

        return reservationRepository
                .findByTransportAdTripDestinationIgnoreCaseAndTransportAdTransportType(
                        destination.trim(),
                        parsedType
                )
                .stream()
                .map(this::toDetailsResponse)
                .toList();
    }
}