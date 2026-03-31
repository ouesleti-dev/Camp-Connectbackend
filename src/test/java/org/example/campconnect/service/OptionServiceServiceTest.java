package org.example.campconnect.Service;

import org.example.campconnect.Entity.OptionService;
import org.example.campconnect.Entity.OptionType;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.OptionServiceRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.OptionServiceRequest;
import org.example.campconnect.dto.OptionServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionServiceServiceTest {

    @Mock
    private OptionServiceRepository optionServiceRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private OptionServiceServiceImpl optionServiceService;

    private OptionService optionService;
    private Vehicle vehicle;
    private OptionServiceRequest optionRequest;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setLicensePlate("TUN-001");
        vehicle.setVehicleType("Car");

        optionService = new OptionService();
        optionService.setOptionId(1L);
        optionService.setName("WiFi");
        optionService.setOptionType(OptionType.WI_FI);
        optionService.setVehicle(vehicle);

        optionRequest = new OptionServiceRequest(
                "WiFi", "WI_FI", 1L
        );
    }

    // ─── createOptionService ──────────────────────────────────────────────
    @Test
    @DisplayName("Should create option service successfully")
    void shouldCreateOptionService() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(optionServiceRepository.save(any(OptionService.class))).thenReturn(optionService);

        OptionServiceResponse result = optionServiceService.createOptionService(optionRequest);

        assertNotNull(result);
        assertEquals("WiFi", result.name());
        assertEquals("WI_FI", result.optionType());
        verify(optionServiceRepository, times(1)).save(any(OptionService.class));
    }

    @Test
    @DisplayName("Should throw when name is blank")
    void shouldThrowWhenNameIsBlank() {
        OptionServiceRequest badRequest = new OptionServiceRequest("", "WI_FI", 1L);

        assertThrows(IllegalArgumentException.class,
                () -> optionServiceService.createOptionService(badRequest));
    }

    @Test
    @DisplayName("Should throw when vehicle not found")
    void shouldThrowWhenVehicleNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        OptionServiceRequest badRequest = new OptionServiceRequest("WiFi", "WI_FI", 99L);

        assertThrows(RuntimeException.class,
                () -> optionServiceService.createOptionService(badRequest));
    }

    // ─── getAllOptionServices ─────────────────────────────────────────────
    @Test
    @DisplayName("Should return all option services")
    void shouldReturnAllOptions() {
        when(optionServiceRepository.findAll()).thenReturn(List.of(optionService));

        List<OptionServiceResponse> result = optionServiceService.getAllOptionServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WiFi", result.get(0).name());
    }

    // ─── getOptionServiceById ─────────────────────────────────────────────
    @Test
    @DisplayName("Should return option service by ID")
    void shouldReturnOptionById() {
        when(optionServiceRepository.findById(1L)).thenReturn(Optional.of(optionService));

        OptionServiceResponse result = optionServiceService.getOptionServiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.optionId());
    }

    @Test
    @DisplayName("Should throw when option not found")
    void shouldThrowWhenOptionNotFound() {
        when(optionServiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> optionServiceService.getOptionServiceById(99L));
    }

    // ─── deleteOptionService ──────────────────────────────────────────────
    @Test
    @DisplayName("Should delete option service successfully")
    void shouldDeleteOption() {
        when(optionServiceRepository.findById(1L)).thenReturn(Optional.of(optionService));
        doNothing().when(optionServiceRepository).deleteById(1L);

        assertDoesNotThrow(() -> optionServiceService.deleteOptionService(1L));
        verify(optionServiceRepository, times(1)).deleteById(1L);
    }

    // ─── getByVehicleId ───────────────────────────────────────────────────
    @Test
    @DisplayName("Should return options by vehicle ID")
    void shouldReturnOptionsByVehicleId() {
        when(optionServiceRepository.findByVehicleVehicleId(1L))
                .thenReturn(List.of(optionService));

        List<OptionServiceResponse> result = optionServiceService.getByVehicleId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).vehicleId());
    }
}