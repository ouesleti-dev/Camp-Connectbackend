package org.example.campconnect.Service;

import org.example.campconnect.dto.EventCreateRequest;
import org.example.campconnect.dto.EventDTO;
import org.example.campconnect.dto.EventUpdateRequest;

import java.util.List;

public interface IEventService {

    EventDTO createEvent(EventCreateRequest request);

    EventDTO getEventById(Long id);

    List<EventDTO> getAllEvents();

    List<EventDTO> getEventsByCamping(Long campingId);

    List<EventDTO> getEventsByStatus(String status);

    EventDTO updateEvent(Long id, EventUpdateRequest request);

    void deleteEvent(Long id);
}