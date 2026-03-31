package org.example.campconnect;

import org.example.campconnect.Entity.Camping;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Service.IEventServiceImp;
import org.example.campconnect.dto.EventCreateRequest;
import org.example.campconnect.dto.EventDTO;
import org.example.campconnect.mapper.EventMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CampingRepository campingRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private IEventServiceImp eventService;

    // ── Test 1 : Créer un event avec succès ──────────────────
    @Test
    @DisplayName("Créer un événement avec succès")
    void shouldCreateEventSuccessfully() {
        // Arrange
        Camping camping = new Camping();
        camping.setCampingId(1L);
        camping.setName("Camping Les Pins");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Nettoyage Forêt");
        event.setCamping(camping);

        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Nettoyage Forêt");
        request.setEventDate(LocalDate.now().plusDays(10));
        request.setMaxParticipants(50);
        request.setStatus("PLANNED");
        request.setCampingId(1L);

        EventDTO expectedDto = new EventDTO();
        expectedDto.setId(1L);
        expectedDto.setTitle("Nettoyage Forêt");
        expectedDto.setCampingName("Camping Les Pins");

        when(campingRepository.findById(1L)).thenReturn(Optional.of(camping));
        when(eventMapper.toEntity(request)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(expectedDto);

        // Act
        EventDTO result = eventService.createEvent(request);

        // Assert
        assertNotNull(result);
        assertEquals("Nettoyage Forêt", result.getTitle());
        assertEquals("Camping Les Pins", result.getCampingName());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // ── Test 2 : Camping introuvable → exception ──────────────
    @Test
    @DisplayName("Lève une exception si le camping est introuvable")
    void shouldThrowWhenCampingNotFound() {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Test Event");
        request.setEventDate(LocalDate.now().plusDays(5));
        request.setMaxParticipants(30);
        request.setStatus("PLANNED");
        request.setCampingId(99L);

        when(campingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(request));

        verify(eventRepository, never()).save(any());
    }

    // ── Test 3 : Récupérer tous les events ───────────────────
    @Test
    @DisplayName("Retourne la liste de tous les événements")
    void shouldReturnAllEvents() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Event 1");

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Event 2");

        EventDTO dto1 = new EventDTO();
        dto1.setId(1L);
        dto1.setTitle("Event 1");

        EventDTO dto2 = new EventDTO();
        dto2.setId(2L);
        dto2.setTitle("Event 2");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        when(eventMapper.toDto(event1)).thenReturn(dto1);
        when(eventMapper.toDto(event2)).thenReturn(dto2);

        List<EventDTO> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    // ── Test 4 : Event introuvable par ID ────────────────────
    @Test
    @DisplayName("Lève une exception si l'événement est introuvable par ID")
    void shouldThrowWhenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> eventService.getEventById(99L));
    }

    // ── Test 5 : Supprimer un event ──────────────────────────
    @Test
    @DisplayName("Supprime un événement existant")
    void shouldDeleteEventSuccessfully() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    // ── Test 6 : Supprimer un event inexistant → exception ───
    @Test
    @DisplayName("Lève une exception lors de la suppression d'un event inexistant")
    void shouldThrowWhenDeletingNonExistentEvent() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> eventService.deleteEvent(99L));

        verify(eventRepository, never()).deleteById(any());
    }
}