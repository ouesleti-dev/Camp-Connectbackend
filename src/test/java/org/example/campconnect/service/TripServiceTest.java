package org.example.campconnect.Service;

import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.TripRequest;
import org.example.campconnect.dto.TripResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    private Trip trip;
    private Vehicle vehicle;
    private TripRequest tripRequest;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setLicensePlate("TUN-001");
        vehicle.setVehicleType("Car");
        vehicle.setCapacity(5L);
        vehicle.setStatus("active");

        trip = new Trip();
        trip.setTripId(1L);
        trip.setDepartureLocation("Tunis");
        trip.setDestination("Sousse");
        trip.setDepartureDate(new Date());
        trip.setDistance(150.0f);
        trip.setVehicle(vehicle);

        tripRequest = new TripRequest(
                "Tunis", "Sousse", new Date(), 150.0f, 1L
        );
    }

    // ─── createTrip ───────────────────────────────────────────────────────
    @Test
    @DisplayName("Should create trip successfully")
    void shouldCreateTrip() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripResponse result = tripService.createTrip(tripRequest);

        assertNotNull(result);
        assertEquals("Tunis", result.departureLocation());
        assertEquals("Sousse", result.destination());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found for trip creation")
    void shouldThrowWhenVehicleNotFoundForTrip() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        TripRequest badRequest = new TripRequest(
                "Tunis", "Sousse", new Date(), 150.0f, 99L
        );

        assertThrows(RuntimeException.class,
                () -> tripService.createTrip(badRequest));
    }

    // ─── getAllTrips ──────────────────────────────────────────────────────
    @Test
    @DisplayName("Should return all trips")
    void shouldReturnAllTrips() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        List<TripResponse> result = tripService.getAllTrips();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tunis", result.get(0).departureLocation());
    }

    @Test
    @DisplayName("Should return empty list when no trips")
    void shouldReturnEmptyListWhenNoTrips() {
        when(tripRepository.findAll()).thenReturn(List.of());

        List<TripResponse> result = tripService.getAllTrips();

        assertTrue(result.isEmpty());
    }

    // ─── getTripById ──────────────────────────────────────────────────────
    @Test
    @DisplayName("Should return trip by ID")
    void shouldReturnTripById() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        TripResponse result = tripService.getTripById(1L);

        assertNotNull(result);
        assertEquals(1L, result.tripId());
    }

    @Test
    @DisplayName("Should throw exception when trip not found")
    void shouldThrowWhenTripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> tripService.getTripById(99L));
    }

    // ─── getTripsByVehicleId ──────────────────────────────────────────────
    @Test
    @DisplayName("Should return trips by vehicle ID")
    void shouldReturnTripsByVehicleId() {
        when(tripRepository.findByVehicleVehicleId(1L)).thenReturn(List.of(trip));

        List<TripResponse> result = tripService.getTripsByVehicleId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).vehicleId());
    }

    // ─── deleteTrip ───────────────────────────────────────────────────────
    @Test
    @DisplayName("Should delete trip successfully")
    void shouldDeleteTrip() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        doNothing().when(tripRepository).deleteById(1L);

        assertDoesNotThrow(() -> tripService.deleteTrip(1L));
        verify(tripRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing trip")
    void shouldThrowWhenDeletingNonExistingTrip() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> tripService.deleteTrip(99L));
    }
}