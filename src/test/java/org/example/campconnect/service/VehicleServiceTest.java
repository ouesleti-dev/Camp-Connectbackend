package org.example.campconnect.Service;

import org.example.campconnect.Entity.User;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.VehicleRequest;
import org.example.campconnect.dto.VehicleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle;
    private VehicleRequest vehicleRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(10L);
        user.setEmail("test@mail.com");
        user.setVehicles(new ArrayList<>());

        vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setLicensePlate("TUN-001");
        vehicle.setVehicleType("Car");
        vehicle.setCapacity(5L);
        vehicle.setStatus("active");

        vehicleRequest = new VehicleRequest(
                "TUN-001", "Car", 5L, "active"
        );
    }

    @Test
    @DisplayName("Should return list of all vehicles")
    void shouldReturnAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<VehicleResponse> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TUN-001", result.get(0).licensePlate());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no vehicles")
    void shouldReturnEmptyListWhenNoVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of());

        List<VehicleResponse> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return vehicle by ID")
    void shouldReturnVehicleById() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        VehicleResponse result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.vehicleId());
        assertEquals("TUN-001", result.licensePlate());
        assertEquals("Car", result.vehicleType());
        assertEquals(5L, result.capacity());
        assertEquals("active", result.status());
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found by ID")
    void shouldThrowWhenVehicleNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleService.getVehicleById(99L));
    }

    @Test
    @DisplayName("Should add vehicle successfully")
    void shouldAddVehicle() {
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setVehicleId(1L);
        savedVehicle.setLicensePlate("TUN-001");
        savedVehicle.setVehicleType("Car");
        savedVehicle.setCapacity(5L);
        savedVehicle.setStatus("active");

        when(vehicleRepository.findByLicensePlate("TUN-001")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        VehicleResponse result = vehicleService.addVehicle(vehicleRequest, "test@mail.com");

        assertNotNull(result);
        assertEquals(1L, result.vehicleId());
        assertEquals("TUN-001", result.licensePlate());
        assertEquals("Car", result.vehicleType());
        assertEquals(5L, result.capacity());
        assertEquals("active", result.status());
        assertEquals(10L, result.ownerId());
        assertEquals("test@mail.com", result.ownerEmail());

        verify(vehicleRepository).findByLicensePlate("TUN-001");
        verify(userRepository).findByEmail("test@mail.com");
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    @DisplayName("Should update vehicle successfully")
    void shouldUpdateVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleRequest updateRequest = new VehicleRequest(
                "TUN-002", "Bus", 20L, "inactive"
        );

        VehicleResponse result = vehicleService.updateVehicle(1L, updateRequest, "test@mail.com");

        assertNotNull(result);
        assertEquals(1L, result.vehicleId());
        assertEquals("TUN-002", result.licensePlate());
        assertEquals("Bus", result.vehicleType());
        assertEquals(20L, result.capacity());
        assertEquals("inactive", result.status());
        assertEquals(10L, result.ownerId());
        assertEquals("test@mail.com", result.ownerEmail());

        verify(vehicleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("test@mail.com");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing vehicle")
    void shouldThrowWhenUpdatingNonExistingVehicle() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleService.updateVehicle(99L, vehicleRequest, "test@mail.com"));

        verify(vehicleRepository).findById(99L);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should delete vehicle successfully")
    void shouldDeleteVehicle() {
        user.setVehicles(new ArrayList<>(List.of(vehicle)));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(vehicleRepository).deleteById(1L);

        assertDoesNotThrow(() -> vehicleService.deleteVehicle(1L, "test@mail.com"));

        assertTrue(user.getVehicles().isEmpty());

        verify(vehicleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).save(user);
        verify(vehicleRepository, times(1)).deleteById(1L);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing vehicle")
    void shouldThrowWhenDeletingNonExistingVehicle() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleService.deleteVehicle(99L, "test@mail.com"));

        verify(vehicleRepository).findById(99L);
        verify(userRepository, never()).findAll();
        verify(vehicleRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should return vehicles by status")
    void shouldReturnVehiclesByStatus() {
        when(vehicleRepository.findByStatus("active")).thenReturn(List.of(vehicle));

        List<VehicleResponse> result = vehicleService.getVehiclesByStatus("active");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("active", result.get(0).status());

        verify(vehicleRepository).findByStatus("active");
    }

    @Test
    @DisplayName("Should return vehicles by type")
    void shouldReturnVehiclesByType() {
        when(vehicleRepository.findByVehicleType("Car")).thenReturn(List.of(vehicle));

        List<VehicleResponse> result = vehicleService.getVehiclesByType("Car");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Car", result.get(0).vehicleType());

        verify(vehicleRepository).findByVehicleType("Car");
    }
}