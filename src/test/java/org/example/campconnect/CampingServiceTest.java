package org.example.campconnect;

import org.example.campconnect.Entity.Camping;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Service.ICampingServiceImp;
import org.example.campconnect.dto.CampingCreateRequest;
import org.example.campconnect.dto.CampingDTO;
import org.example.campconnect.mapper.CampingMapper;
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
class CampingServiceTest {

    @Mock
    private CampingRepository campingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CampingMapper campingMapper;

    @InjectMocks
    private ICampingServiceImp campingService;

    // ── Test 1 : Créer un camping avec succès ────────────────
    @Test
    @DisplayName("Créer un camping avec succès")
    void shouldCreateCampingSuccessfully() {
        CampingCreateRequest request = new CampingCreateRequest();
        request.setName("Camping Les Pins");
        request.setAddress("Route de la Forêt");
        request.setPostalCode("8142");
        request.setStatus("OPEN");

        Camping camping = new Camping();
        camping.setCampingId(1L);
        camping.setName("Camping Les Pins");

        CampingDTO expectedDto = new CampingDTO();
        expectedDto.setCampingId(1L);
        expectedDto.setName("Camping Les Pins");

        when(campingRepository.existsByName("Camping Les Pins")).thenReturn(false);
        when(campingMapper.toEntity(request)).thenReturn(camping);
        when(campingRepository.save(any(Camping.class))).thenReturn(camping);
        when(campingMapper.toDto(camping)).thenReturn(expectedDto);

        CampingDTO result = campingService.createCamping(request);

        assertNotNull(result);
        assertEquals("Camping Les Pins", result.getName());
        verify(campingRepository, times(1)).save(any(Camping.class));
    }

    // ── Test 2 : Doublon nom → exception ─────────────────────
    @Test
    @DisplayName("Lève une exception si le nom du camping existe déjà")
    void shouldThrowWhenCampingNameAlreadyExists() {
        CampingCreateRequest request = new CampingCreateRequest();
        request.setName("Camping Les Pins");
        request.setAddress("Route de la Forêt");
        request.setPostalCode("8142");
        request.setStatus("OPEN");

        when(campingRepository.existsByName("Camping Les Pins")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> campingService.createCamping(request));

        verify(campingRepository, never()).save(any());
    }

    // ── Test 3 : Camping introuvable → exception ─────────────
    @Test
    @DisplayName("Lève une exception si le camping est introuvable")
    void shouldThrowWhenCampingNotFound() {
        when(campingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> campingService.getCampingById(99L));
    }

    // ── Test 4 : Supprimer camping avec events actifs ────────
    @Test
    @DisplayName("Lève une exception si le camping a des events actifs")
    void shouldThrowWhenCampingHasActiveEvents() {
        Camping camping = new Camping();
        camping.setCampingId(1L);

        org.example.campconnect.Entity.Event event =
                new org.example.campconnect.Entity.Event();
        event.setStatus("PLANNED");

        when(campingRepository.findById(1L)).thenReturn(Optional.of(camping));
        when(eventRepository.findByCamping_CampingId(1L))
                .thenReturn(List.of(event));

        assertThrows(IllegalArgumentException.class,
                () -> campingService.deleteCamping(1L));

        verify(campingRepository, never()).deleteById(any());
    }
}