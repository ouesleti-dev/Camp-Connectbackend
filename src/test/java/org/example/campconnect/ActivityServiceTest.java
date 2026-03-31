package org.example.campconnect;

import org.example.campconnect.Entity.Activity;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Repository.ActivityRepository;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Service.IActivityServiceImp;
import org.example.campconnect.dto.ActivityCreateRequest;
import org.example.campconnect.dto.ActivityDTO;
import org.example.campconnect.mapper.ActivityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CampingRepository campingRepository;

    @Mock
    private ActivityMapper activityMapper;

    @InjectMocks
    private IActivityServiceImp activityService;

    // ── Test 1 : Créer activité sans eventId ni campingId ────
    @Test
    @DisplayName("Lève une exception si ni eventId ni campingId fourni")
    void shouldThrowWhenNoEventNorCamping() {
        ActivityCreateRequest request = new ActivityCreateRequest();
        request.setName("Randonnée");
        request.setDuration(120);
        request.setDifficulty("EASY");
        // eventId et campingId null

        assertThrows(IllegalArgumentException.class,
                () -> activityService.createActivity(request));

        verify(activityRepository, never()).save(any());
    }

    // ── Test 2 : Créer activité avec event existant ──────────
    @Test
    @DisplayName("Crée une activité liée à un événement existant")
    void shouldCreateActivityWithEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Nettoyage Forêt");

        ActivityCreateRequest request = new ActivityCreateRequest();
        request.setName("Ramassage déchets");
        request.setDuration(120);
        request.setDifficulty("EASY");
        request.setEventId(1L);

        Activity activity = new Activity();
        activity.setId(1L);
        activity.setName("Ramassage déchets");

        ActivityDTO expectedDto = new ActivityDTO();
        expectedDto.setId(1L);
        expectedDto.setName("Ramassage déchets");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(activityRepository.existsByNameAndEvent_Id(
                "Ramassage déchets", 1L)).thenReturn(false);
        when(activityMapper.toEntity(request)).thenReturn(activity);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(activityMapper.toDto(activity)).thenReturn(expectedDto);

        ActivityDTO result = activityService.createActivity(request);

        assertNotNull(result);
        assertEquals("Ramassage déchets", result.getName());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    // ── Test 3 : Anti-doublon activité ───────────────────────
    @Test
    @DisplayName("Lève une exception si l'activité existe déjà dans cet event")
    void shouldThrowWhenActivityAlreadyExistsInEvent() {
        Event event = new Event();
        event.setId(1L);

        ActivityCreateRequest request = new ActivityCreateRequest();
        request.setName("Ramassage déchets");
        request.setDuration(60);
        request.setDifficulty("EASY");
        request.setEventId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(activityRepository.existsByNameAndEvent_Id(
                "Ramassage déchets", 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> activityService.createActivity(request));

        verify(activityRepository, never()).save(any());
    }

    // ── Test 4 : Activity introuvable → exception ────────────
    @Test
    @DisplayName("Lève une exception si l'activité est introuvable")
    void shouldThrowWhenActivityNotFound() {
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> activityService.getActivityById(99L));
    }
}