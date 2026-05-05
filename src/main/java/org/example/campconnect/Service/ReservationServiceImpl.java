package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.OptionServiceRepository;
import org.example.campconnect.Repository.ReservationRepository;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.OptionServiceResponse;
import org.example.campconnect.dto.OptionResponse;
import org.example.campconnect.dto.ReservationDetailsResponse;
import org.example.campconnect.dto.ReservationRequest;
import org.example.campconnect.dto.ReservationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final TransportAdRepository transportAdRepository;
    private final UserRepository userRepository;
    private final OptionServiceRepository optionServiceRepository;

    private OptionResponse toOptionResponse(OptionService optionService) {
        return new OptionResponse(
                optionService.getOptionId(),
                optionService.getName(),
                optionService.getPrice(),
                optionService.getOptionType() != null
                        ? optionService.getOptionType().name()
                        : null
        );
    }

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
                reservation.getTotalPrice(),
                trip != null ? trip.getDepartureLocation() : null,
                trip != null ? trip.getDestination() : null,
                userEmail,
                reservation.getSelectedOptions() != null
                        ? reservation.getSelectedOptions()
                        .stream()
                        .map(this::toOptionResponse)
                        .toList()
                        : List.of()
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

    private void validateRequest(ReservationRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("La requete est vide");
        }

        if (req.reservationDate() == null) {
            throw new IllegalArgumentException("La date de reservation est requise");
        }

        if (req.seatCount() == null || req.seatCount() <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit etre positif");
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

    private long availableSeats(TransportAd ad) {
        return ad.getAvailableSeats() != null ? ad.getAvailableSeats() : 0L;
    }

    private long seatCount(Long seatCount) {
        return seatCount != null ? seatCount : 0L;
    }

    private void ensureSeatsAvailable(TransportAd ad, long requestedSeats) {
        if (availableSeats(ad) < requestedSeats) {
            throw new IllegalArgumentException("Places insuffisantes");
        }
    }

    private boolean isSameTransportAd(TransportAd currentAd, TransportAd requestedAd) {
        return currentAd != null
                && requestedAd != null
                && currentAd.getAdId() != null
                && currentAd.getAdId().equals(requestedAd.getAdId());
    }

    private List<OptionService> getSelectedOptions(ReservationRequest req) {
        if (req.optionIds() == null || req.optionIds().isEmpty()) {
            return List.of();
        }

        return optionServiceRepository.findAllById(req.optionIds());
    }

    private Float calculateTotalPrice(TransportAd ad, Long seatCount, List<OptionService> selectedOptions) {
        float adPrice = ad != null ? ad.getPrice() : 0f;
        long seats = seatCount(seatCount);

        float optionsTotal = selectedOptions == null
                ? 0f
                : selectedOptions.stream()
                .map(OptionService::getPrice)
                .filter(Objects::nonNull)
                .reduce(0f, Float::sum);

        return (adPrice * seats) + optionsTotal;
    }

    private void applyOptionsAndTotalPrice(
            Reservation reservation,
            TransportAd ad,
            ReservationRequest req
    ) {
        List<OptionService> selectedOptions = getSelectedOptions(req);
        reservation.setSelectedOptions(selectedOptions);
        reservation.setTotalPrice(calculateTotalPrice(ad, req.seatCount(), selectedOptions));
    }

    private void detachReservationFromUsers(Long reservationId) {
        userRepository.findAll().forEach(user -> {
            if (user.getReservations() != null) {
                boolean removed = user.getReservations()
                        .removeIf(r -> r != null && reservationId.equals(r.getReservationId()));
                if (removed) {
                    userRepository.save(user);
                }
            }
        });
    }

    private TransportType parseTransportType(String transportType) {
        if (transportType == null || transportType.isBlank()) {
            throw new IllegalArgumentException("Le type transport est requis");
        }

        return Arrays.stream(TransportType.values())
                .filter(type -> type.name().equalsIgnoreCase(transportType.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Type invalide. Valeurs possibles : " + Arrays.toString(TransportType.values())
                ));
    }

    @Override
    public ReservationResponse createReservation(ReservationRequest req, String userEmail) {
        validateRequest(req);

        TransportAd ad = getTransportAdById(req.transportAdId());
        ensureSeatsAvailable(ad, req.seatCount());

        User user = getUserByEmail(userEmail);

        Reservation reservation = new Reservation();
        reservation.setReservationDate(req.reservationDate());
        reservation.setSeatCount(req.seatCount());
        reservation.setStatus(req.status());
        reservation.setTransportAd(ad);

        applyOptionsAndTotalPrice(reservation, ad, req);

        Reservation saved = reservationRepository.save(reservation);

        ad.setAvailableSeats(availableSeats(ad) - req.seatCount());
        transportAdRepository.save(ad);

        if (user.getReservations() == null) {
            user.setReservations(new ArrayList<>());
        }

        user.getReservations().add(saved);
        userRepository.save(user);

        return toResponse(saved, userEmail);
    }

    @Override
    public ReservationResponse updateReservation(Long id, ReservationRequest req, String userEmail) {
        validateRequest(req);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation introuvable"));

        TransportAd currentAd = reservation.getTransportAd();
        TransportAd requestedAd = getTransportAdById(req.transportAdId());
        long previousSeatCount = seatCount(reservation.getSeatCount());

        if (isSameTransportAd(currentAd, requestedAd)) {
            long additionalSeats = req.seatCount() - previousSeatCount;

            if (additionalSeats > 0) {
                ensureSeatsAvailable(currentAd, additionalSeats);
            }

            currentAd.setAvailableSeats(availableSeats(currentAd) - additionalSeats);
            transportAdRepository.save(currentAd);
        } else {
            ensureSeatsAvailable(requestedAd, req.seatCount());

            if (currentAd != null) {
                currentAd.setAvailableSeats(availableSeats(currentAd) + previousSeatCount);
                transportAdRepository.save(currentAd);
            }

            requestedAd.setAvailableSeats(availableSeats(requestedAd) - req.seatCount());
            transportAdRepository.save(requestedAd);
        }

        reservation.setReservationDate(req.reservationDate());
        reservation.setSeatCount(req.seatCount());
        reservation.setStatus(req.status());
        reservation.setTransportAd(requestedAd);

        applyOptionsAndTotalPrice(reservation, requestedAd, req);

        Reservation updated = reservationRepository.save(reservation);

        return toResponse(updated, userEmail);
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation introuvable"));

        TransportAd ad = reservation.getTransportAd();

        if (ad != null) {
            ad.setAvailableSeats(availableSeats(ad) + seatCount(reservation.getSeatCount()));

            if (ad.getReservations() != null) {
                ad.getReservations().removeIf(r -> r != null && id.equals(r.getReservationId()));
            }

            transportAdRepository.save(ad);
        }

        detachReservationFromUsers(id);
        reservationRepository.delete(reservation);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation introuvable"));

        String userEmail = reservationRepository.findUserEmailByReservationId(id);
        return toResponse(reservation, userEmail);
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