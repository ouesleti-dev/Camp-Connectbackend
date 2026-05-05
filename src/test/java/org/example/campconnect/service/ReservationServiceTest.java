package org.example.campconnect.Service;

import org.example.campconnect.Entity.Reservation;
import org.example.campconnect.Entity.Role;
import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.TransportType;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Entity.Vehicle;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        assertEquals(3L, transportAd.getAvailableSeats());
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

    @Test
    @DisplayName("Should return all reservations")
    void shouldReturnAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationResponse> result = reservationService.getAllReservations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONFIRMED", result.get(0).status());
    }

    @Test
    @DisplayName("Should return reservation by ID")
    void shouldReturnReservationById() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

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

    @Test
    @DisplayName("Should update reservation on same transport ad and recalculate seats")
    void shouldUpdateReservationOnSameAd() {
        transportAd.setAvailableSeats(3L);
        ReservationRequest updateRequest = new ReservationRequest(
                new Date(), 4L, "CONFIRMED", 1L
        );

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(transportAdRepository.findById(1L)).thenReturn(Optional.of(transportAd));
        when(transportAdRepository.save(any(TransportAd.class))).thenReturn(transportAd);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse result = reservationService.updateReservation(
                1L, updateRequest, "yossri@gmail.com"
        );

        assertNotNull(result);
        assertEquals(4L, result.seatCount());
        assertEquals(1L, result.transportAdId());
        assertEquals(1L, transportAd.getAvailableSeats());
    }

    @Test
    @DisplayName("Should update reservation to another transport ad and rebalance seats")
    void shouldUpdateReservationToAnotherAd() {
        TransportAd newAd = new TransportAd();
        newAd.setAdId(2L);
        newAd.setPrice(30.0f);
        newAd.setAvailableSeats(6L);
        newAd.setTransportType(TransportType.Public_transport);
        newAd.setTrip(transportAd.getTrip());

        ReservationRequest updateRequest = new ReservationRequest(
                new Date(), 3L, "CONFIRMED", 2L
        );

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(transportAdRepository.findById(2L)).thenReturn(Optional.of(newAd));
        when(transportAdRepository.save(any(TransportAd.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse result = reservationService.updateReservation(
                1L, updateRequest, "yossri@gmail.com"
        );

        assertNotNull(result);
        assertEquals(3L, result.seatCount());
        assertEquals(2L, result.transportAdId());
        assertEquals(7L, transportAd.getAvailableSeats());
        assertEquals(3L, newAd.getAvailableSeats());
    }

    @Test
    @DisplayName("Should delete reservation and increment seats back")
    void shouldDeleteReservationAndIncrementSeats() {
        user.getReservations().add(reservation);
        transportAd.setAvailableSeats(3L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(transportAdRepository.save(any(TransportAd.class))).thenReturn(transportAd);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(reservationRepository).delete(reservation);

        assertDoesNotThrow(() -> reservationService.deleteReservation(1L));

        assertEquals(5L, transportAd.getAvailableSeats());
        assertTrue(user.getReservations().isEmpty());

        verify(reservationRepository, times(1)).findById(1L);
        verify(transportAdRepository, times(1)).save(transportAd);
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).save(user);
        verify(reservationRepository, times(1)).delete(reservation);
    }
}
