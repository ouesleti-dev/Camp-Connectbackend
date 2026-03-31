package org.example.campconnect.Service;

import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.ReservationRepository;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ReservationRequest;
import org.example.campconnect.dto.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TransportAdRepository transportAdRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation reservation;
    private TransportAd transportAd;
    private User user;
    private ReservationRequest reservationRequest;

    @BeforeEach
    void setUp() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setLicensePlate("TUN-001");

        Trip trip = new Trip();
        trip.setTripId(1L);
        trip.setDepartureLocation("Tunis");
        trip.setDestination("Sousse");
        trip.setVehicle(vehicle);

        transportAd = new TransportAd();
        transportAd.setAdId(1L);
        transportAd.setPrice(25.0f);
        transportAd.setAvailableSeats(5L);
        transportAd.setTransportType(TransportType.Ride_sharing);
        transportAd.setTrip(trip);

        user = User.builder()
                .idUser(1L)
                .firstName("Yossri")
                .lastName("Test")
                .email("yossri@gmail.com")
                .password("password")
                .role(Role.CAMPOWNER)
                .enabled(true)
                .vehicles(new ArrayList<>())
                .reservations(new ArrayList<>())
                .build();

        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setReservationDate(new Date());
        reservation.setSeatCount(2L);
        reservation.setStatus("CONFIRMED");
        reservation.setTransportAd(transportAd);

        reservationRequest = new ReservationRequest(
                new Date(), 2L, "CONFIRMED", 1L
        );
    }

    // ─── createReservation ────────────────────────────────────────────────
    @Test
    @DisplayName("Should create reservation successfully")
    void shouldCreateReservation() {
        when(transportAdRepository.findById(1L)).thenReturn(Optional.of(transportAd));
        when(userRepository.findByEmail("yossri@gmail.com")).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(transportAdRepository.save(any(TransportAd.class))).thenReturn(transportAd);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ReservationResponse result = reservationService.createReservation(
                reservationRequest, "yossri@gmail.com"
        );

        assertNotNull(result);
        assertEquals(2L, result.seatCount());
        assertEquals("CONFIRMED", result.status());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw when not enough seats available")
    void shouldThrowWhenNotEnoughSeats() {
        transportAd.setAvailableSeats(1L);
        when(transportAdRepository.findById(1L)).thenReturn(Optional.of(transportAd));

        ReservationRequest bigRequest = new ReservationRequest(
                new Date(), 5L, "CONFIRMED", 1L
        );

        assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(bigRequest, "yossri@gmail.com"));
    }

    @Test
    @DisplayName("Should throw when transport ad not found")
    void shouldThrowWhenAdNotFound() {
        when(transportAdRepository.findById(99L)).thenReturn(Optional.empty());

        ReservationRequest badRequest = new ReservationRequest(
                new Date(), 1L, "CONFIRMED", 99L
        );

        assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(badRequest, "yossri@gmail.com"));
    }

    // ─── getAllReservations ───────────────────────────────────────────────
    @Test
    @DisplayName("Should return all reservations")
    void shouldReturnAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        when(reservationRepository.findUserEmailByReservationId(1L)).thenReturn("yossri@gmail.com");

        List<ReservationResponse> result = reservationService.getAllReservations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).status());
    }

    // ─── getReservationById ───────────────────────────────────────────────
    @Test
    @DisplayName("Should return reservation by ID")
    void shouldReturnReservationById() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.findUserEmailByReservationId(1L)).thenReturn("yossri@gmail.com");

        ReservationResponse result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.reservationId());
    }

    @Test
    @DisplayName("Should throw when reservation not found")
    void shouldThrowWhenReservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> reservationService.getReservationById(99L));
    }

    // ─── deleteReservation ────────────────────────────────────────────────
    @Test
    @DisplayName("Should delete reservation and increment seats back")
    void shouldDeleteReservationAndIncrementSeats() {
        when(reservationRepository.findTransportAdIdByReservationId(1L)).thenReturn(1L);
        when(reservationRepository.findSeatCountByReservationId(1L)).thenReturn(2L);
        doNothing().when(reservationRepository).deleteUserReservationLink(1L);
        doNothing().when(reservationRepository).incrementSeats(1L, 2L);
        doNothing().when(reservationRepository).deleteByIdNative(1L);

        assertDoesNotThrow(() -> reservationService.deleteReservation(1L));
        verify(reservationRepository, times(1)).deleteUserReservationLink(1L);
        verify(reservationRepository, times(1)).incrementSeats(1L, 2L);
        verify(reservationRepository, times(1)).deleteByIdNative(1L);
    }
}