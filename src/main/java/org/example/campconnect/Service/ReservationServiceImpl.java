package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Reservation;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.ReservationRepository;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ReservationRequest;
import org.example.campconnect.dto.ReservationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final TransportAdRepository transportAdRepository;
    private final UserRepository userRepository;

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

    private void validateRequest(ReservationRequest req) {
        if (req.reservationDate() == null) {
            throw new IllegalArgumentException("La date de reservation est requise");
        }
        if (req.seatCount() == null || req.seatCount() <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit etre positif");
        }
        if (req.status() == null || req.status().isBlank()) {
            throw new IllegalArgumentException("Le statut est requis");
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
                .orElseThrow(() -> new RuntimeException("User introuvable"));
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest req, String userEmail) {
        validateRequest(req);

        TransportAd ad = transportAdRepository.findById(req.transportAdId())
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        if (ad.getAvailableSeats() < req.seatCount()) {
            throw new RuntimeException("Pas assez de places disponibles");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User introuvable"));

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

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest req, String userEmail) {
        validateRequest(req);

        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation introuvable avec l'id : " + id));
        TransportAd oldAd = existing.getTransportAd();
        TransportAd newAd = getTransportAdById(req.transportAdId());

        if (oldAd != null && oldAd.getAdId().equals(newAd.getAdId())) {
            long restoredSeats = oldAd.getAvailableSeats() + existing.getSeatCount();
            if (restoredSeats < req.seatCount()) {
                throw new IllegalArgumentException("Nombre de places insuffisant");
            }
            oldAd.setAvailableSeats(restoredSeats - req.seatCount());
            transportAdRepository.save(oldAd);
        } else {
            if (oldAd != null) {
                oldAd.setAvailableSeats(oldAd.getAvailableSeats() + existing.getSeatCount());
                transportAdRepository.save(oldAd);
            }
            if (newAd.getAvailableSeats() < req.seatCount()) {
                throw new IllegalArgumentException("Nombre de places insuffisant");
            }
            newAd.setAvailableSeats(newAd.getAvailableSeats() - req.seatCount());
            transportAdRepository.save(newAd);
        }

        String previousUserEmail = reservationRepository.findUserEmailByReservationId(id);
        User currentUser = getUserByEmail(userEmail);

        existing.setReservationDate(req.reservationDate());
        existing.setSeatCount(req.seatCount());
        existing.setStatus(req.status());
        existing.setTransportAd(newAd);

        Reservation saved = reservationRepository.save(existing);

        if (previousUserEmail != null && !previousUserEmail.equals(userEmail)) {
            User previousUser = getUserByEmail(previousUserEmail);
            if (previousUser.getReservations() != null) {
                previousUser.getReservations().removeIf(r -> r.getReservationId().equals(id));
                userRepository.save(previousUser);
            }
        }

        if (currentUser.getReservations() == null) {
            currentUser.setReservations(new ArrayList<>());
        }
        boolean alreadyLinked = currentUser.getReservations().stream()
                .anyMatch(r -> r.getReservationId().equals(id));
        if (!alreadyLinked) {
            currentUser.getReservations().add(saved);
            userRepository.save(currentUser);
        }

        return toResponse(saved, userEmail);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Long transportAdId = reservationRepository.findTransportAdIdByReservationId(id);
        Long seatCount = reservationRepository.findSeatCountByReservationId(id);

        reservationRepository.deleteUserReservationLink(id);

        if (transportAdId != null && seatCount != null) {
            reservationRepository.incrementSeats(transportAdId, seatCount);
        }

        reservationRepository.deleteByIdNative(id);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation introuvable avec l'id : " + id));
        return toResponse(reservation, reservationRepository.findUserEmailByReservationId(id));
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(r -> toResponse(r, reservationRepository.findUserEmailByReservationId(r.getReservationId())))
                .toList();
    }

    @Override
    public List<ReservationResponse> getByStatus(String status) {
        return reservationRepository.findByStatus(status)
                .stream()
                .map(r -> toResponse(r, reservationRepository.findUserEmailByReservationId(r.getReservationId())))
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
                .map(r -> toResponse(r, reservationRepository.findUserEmailByReservationId(r.getReservationId())))
                .toList();
    }
}
