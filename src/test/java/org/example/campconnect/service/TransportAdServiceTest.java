package org.example.campconnect.Service;

import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.TransportType;
import org.example.campconnect.Entity.Trip;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.TransportAdRepository;
import org.example.campconnect.Repository.TripRepository;
import org.example.campconnect.dto.TransportAdRequest;
import org.example.campconnect.dto.TransportAdResponse;
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
class TransportAdServiceTest {

    @Mock
    private TransportAdRepository transportAdRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TransportAdServiceImpl transportAdService;

    private TransportAd transportAd;
    private Trip trip;
    private Vehicle vehicle;
    private TransportAdRequest adRequest;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setLicensePlate("TUN-001");

        trip = new Trip();
        trip.setTripId(1L);
        trip.setDepartureLocation("Tunis");
        trip.setDestination("Sousse");
        trip.setDepartureDate(new Date());
        trip.setDistance(150.0f);
        trip.setVehicle(vehicle);

        transportAd = new TransportAd();
        transportAd.setAdId(1L);
        transportAd.setPrice(25.0f);
        transportAd.setAvailableSeats(5L);
        transportAd.setTransportType(TransportType.Ride_sharing);
        transportAd.setTrip(trip);

        adRequest = new TransportAdRequest(25.0f, 5L, "Ride_sharing", 1L);
    }

    // ─── createTransportAd ────────────────────────────────────────────────
    @Test
    @DisplayName("Should create transport ad successfully")
    void shouldCreateTransportAd() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(transportAdRepository.save(any(TransportAd.class))).thenReturn(transportAd);

        TransportAdResponse result = transportAdService.createTransportAd(adRequest);

        assertNotNull(result);
        assertEquals(25.0f, result.price());
        assertEquals(5L, result.availableSeats());
        verify(transportAdRepository, times(1)).save(any(TransportAd.class));
    }

    @Test
    @DisplayName("Should throw when trip not found")
    void shouldThrowWhenTripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        TransportAdRequest badRequest = new TransportAdRequest(25.0f, 5L, "Ride_sharing", 99L);

        assertThrows(RuntimeException.class,
                () -> transportAdService.createTransportAd(badRequest));
    }

    // ─── getAllTransportAds ───────────────────────────────────────────────
    @Test
    @DisplayName("Should return all transport ads")
    void shouldReturnAllAds() {
        when(transportAdRepository.findAll()).thenReturn(List.of(transportAd));

        List<TransportAdResponse> result = transportAdService.getAllTransportAds();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(25.0f, result.get(0).price());
    }

    // ─── getTransportAdById ───────────────────────────────────────────────
    @Test
    @DisplayName("Should return transport ad by ID")
    void shouldReturnAdById() {
        when(transportAdRepository.findById(1L)).thenReturn(Optional.of(transportAd));

        TransportAdResponse result = transportAdService.getTransportAdById(1L);

        assertNotNull(result);
        assertEquals(1L, result.adId());
    }

    @Test
    @DisplayName("Should throw when ad not found")
    void shouldThrowWhenAdNotFound() {
        when(transportAdRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transportAdService.getTransportAdById(99L));
    }

    // ─── deleteTransportAd ────────────────────────────────────────────────
    @Test
    @DisplayName("Should delete transport ad successfully")
    void shouldDeleteAd() {
        when(transportAdRepository.findById(1L)).thenReturn(Optional.of(transportAd));

        assertDoesNotThrow(() -> transportAdService.deleteTransportAd(1L));
        verify(transportAdRepository, atLeastOnce()).save(any(TransportAd.class));
    }

    // ─── getByTransportType ───────────────────────────────────────────────
    @Test
    @DisplayName("Should return ads by transport type")
    void shouldReturnAdsByType() {
        when(transportAdRepository.findByTransportType(TransportType.Ride_sharing))
                .thenReturn(List.of(transportAd));

        List<TransportAdResponse> result = transportAdService.getByTransportType("Ride_sharing");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ride_sharing", result.get(0).transportType());
    }
}