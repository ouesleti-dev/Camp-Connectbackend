package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IEventService;
import org.example.campconnect.dto.EventCreateRequest;
import org.example.campconnect.dto.EventDTO;
import org.example.campconnect.dto.EventUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;

    // Accessible à tous les utilisateurs connectés
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/camping/{campingId}")
    public ResponseEntity<List<EventDTO>> getByCamping(@PathVariable Long campingId) {
        return ResponseEntity.ok(eventService.getEventsByCamping(campingId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(eventService.getEventsByStatus(status));
    }

    // Réservé aux CAMPOWNER et ADMIN
    @PostMapping
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<EventDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody EventUpdateRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}