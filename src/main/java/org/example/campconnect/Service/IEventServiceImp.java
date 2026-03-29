package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Camping;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.dto.EventCreateRequest;
import org.example.campconnect.dto.EventDTO;
import org.example.campconnect.dto.EventUpdateRequest;
import org.example.campconnect.mapper.EventMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IEventServiceImp implements IEventService {

    private final EventRepository eventRepository;
    private final CampingRepository campingRepository;
    private final EventMapper eventMapper;

    @Override
    public EventDTO createEvent(EventCreateRequest request) {
        Camping camping = campingRepository.findById(request.getCampingId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Camping introuvable avec l'ID : " + request.getCampingId()));

        Event event = eventMapper.toEntity(request);
        event.setCamping(camping);

        Event saved = eventRepository.save(event);
        return eventMapper.toDto(saved);
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable avec l'ID : " + id));
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByCamping(Long campingId) {
        if (!campingRepository.existsById(campingId))
            throw new IllegalArgumentException("Camping introuvable avec l'ID : " + campingId);
        return eventRepository.findByCamping_CampingId(campingId)
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByStatus(String status) {
        return eventRepository.findByStatus(status)
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO updateEvent(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable avec l'ID : " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank())
            event.setTitle(request.getTitle());
        if (request.getEventDate() != null)
            event.setEventDate(request.getEventDate());
        if (request.getMaxParticipants() != null)
            event.setMaxParticipants(request.getMaxParticipants());
        if (request.getStatus() != null && !request.getStatus().isBlank())
            event.setStatus(request.getStatus());
        if (request.getWasteCollected() != null)
            event.setWasteCollected(request.getWasteCollected());
        if (request.getCampingId() != null) {
            Camping camping = campingRepository.findById(request.getCampingId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Camping introuvable avec l'ID : " + request.getCampingId()));
            event.setCamping(camping);
        }

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id))
            throw new IllegalArgumentException("Événement introuvable avec l'ID : " + id);
        eventRepository.deleteById(id);
    }
}