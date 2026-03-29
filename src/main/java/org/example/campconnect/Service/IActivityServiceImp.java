package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Activity;
import org.example.campconnect.Entity.Camping;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Repository.ActivityRepository;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.dto.ActivityCreateRequest;
import org.example.campconnect.dto.ActivityDTO;
import org.example.campconnect.dto.ActivityUpdateRequest;
import org.example.campconnect.mapper.ActivityMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IActivityServiceImp implements IActivityService {

    private final ActivityRepository activityRepository;
    private final EventRepository eventRepository;
    private final CampingRepository campingRepository;
    private final ActivityMapper activityMapper;

    @Override
    public ActivityDTO createActivity(ActivityCreateRequest request) {
        // ⭐ Validation : au moins eventId ou campingId doit être fourni
        if (request.getEventId() == null && request.getCampingId() == null)
            throw new IllegalArgumentException(
                    "Une activité doit être liée à un événement ou à un camping");

        Activity activity = activityMapper.toEntity(request);

        // Lier à l'Event si fourni
        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Événement introuvable avec l'ID : " + request.getEventId()));

            // ⭐ Anti-doublon : même nom dans le même event
            if (activityRepository.existsByNameAndEvent_Id(request.getName(), request.getEventId()))
                throw new IllegalArgumentException(
                        "Une activité '" + request.getName() + "' existe déjà dans cet événement");

            activity.setEvent(event);
        }

        // Lier au Camping si fourni
        if (request.getCampingId() != null) {
            Camping camping = campingRepository.findById(request.getCampingId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Camping introuvable avec l'ID : " + request.getCampingId()));

            // ⭐ Anti-doublon : même nom dans le même camping
            if (activityRepository.existsByNameAndCamping_CampingId(request.getName(), request.getCampingId()))
                throw new IllegalArgumentException(
                        "Une activité '" + request.getName() + "' existe déjà dans ce camping");

            activity.setCamping(camping);
        }

        return activityMapper.toDto(activityRepository.save(activity));
    }

    @Override
    public ActivityDTO getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Activité introuvable avec l'ID : " + id));
        return activityMapper.toDto(activity);
    }

    @Override
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAll()
                .stream()
                .map(activity -> {
                    ActivityDTO dto = activityMapper.toDto(activity);
                    dto.setTotalParticipations((int) activityRepository
                            .countParticipationsByActivityId(activity.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> getActivitiesByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId))
            throw new IllegalArgumentException(
                    "Événement introuvable avec l'ID : " + eventId);
        return activityRepository.findByEvent_Id(eventId)
                .stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> getActivitiesByCamping(Long campingId) {
        if (!campingRepository.existsById(campingId))
            throw new IllegalArgumentException(
                    "Camping introuvable avec l'ID : " + campingId);
        return activityRepository.findByCamping_CampingId(campingId)
                .stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> getActivitiesByDifficulty(String difficulty) {
        return activityRepository.findByDifficulty(difficulty)
                .stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDTO updateActivity(Long id, ActivityUpdateRequest request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Activité introuvable avec l'ID : " + id));

        // Mise à jour partielle — seuls les champs non-null sont modifiés
        if (request.getName() != null && !request.getName().isBlank())
            activity.setName(request.getName());
        if (request.getDescription() != null)
            activity.setDescription(request.getDescription());
        if (request.getDuration() != null)
            activity.setDuration(request.getDuration());
        if (request.getDifficulty() != null && !request.getDifficulty().isBlank())
            activity.setDifficulty(request.getDifficulty());

        // Changer l'event lié
        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Événement introuvable avec l'ID : " + request.getEventId()));
            activity.setEvent(event);
        }

        // Changer le camping lié
        if (request.getCampingId() != null) {
            Camping camping = campingRepository.findById(request.getCampingId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Camping introuvable avec l'ID : " + request.getCampingId()));
            activity.setCamping(camping);
        }

        return activityMapper.toDto(activityRepository.save(activity));
    }

    @Override
    public void deleteActivity(Long id) {
        // ⭐ Vérification : l'activité existe avant suppression
        if (!activityRepository.existsById(id))
            throw new IllegalArgumentException(
                    "Activité introuvable avec l'ID : " + id);

        // La suppression des participations liées est gérée par
        // cascade = CascadeType.REMOVE défini dans Activity.java
        activityRepository.deleteById(id);
    }
}

