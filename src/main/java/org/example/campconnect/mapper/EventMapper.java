package org.example.campconnect.mapper;



import org.example.campconnect.Entity.Event;
import org.example.campconnect.dto.EventCreateRequest;
import org.example.campconnect.dto.EventDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EventDTO toDto(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDate(event.getEventDate() != null ? FORMATTER.format(event.getEventDate()) : null)
                .maxParticipants(event.getMaxParticipants())
                .status(event.getStatus())
                .wasteCollected(event.getWasteCollected())
                .campingId(event.getCamping() != null ? event.getCamping().getCampingId() : null)
                .campingName(event.getCamping() != null ? event.getCamping().getName() : null)
                .build();
    }

    public Event toEntity(EventCreateRequest req) {
        Event event = new Event();
        event.setTitle(req.getTitle());
        event.setEventDate(req.getEventDate());
        event.setMaxParticipants(req.getMaxParticipants());
        event.setStatus(req.getStatus());
        event.setWasteCollected(req.getWasteCollected());
        return event;
    }
}