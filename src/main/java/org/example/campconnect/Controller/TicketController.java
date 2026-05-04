package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ITicketService;
import org.example.campconnect.dto.TicketDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketService;

    // ⭐ Générer un ticket pour un event
    @PostMapping("/generate/{eventId}")
    public ResponseEntity<TicketDTO> generate(
            @PathVariable Long eventId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.generateTicket(
                        eventId, authentication.getName()));
    }

    // Mes tickets
    @GetMapping("/my")
    public ResponseEntity<List<TicketDTO>> getMyTickets(
            Authentication authentication) {
        return ResponseEntity.ok(
                ticketService.getMyTickets(authentication.getName()));
    }

    // Tickets d'un event (CAMPOWNER et ADMIN)
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<List<TicketDTO>> getByEvent(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(
                ticketService.getTicketsByEvent(eventId));
    }

    // ⭐ Valider un ticket par son code (scan QR)
    @PutMapping("/validate/{ticketCode}")
    @PreAuthorize("hasAnyRole('CAMPOWNER','ADMIN')")
    public ResponseEntity<TicketDTO> validate(
            @PathVariable String ticketCode) {
        return ResponseEntity.ok(
                ticketService.validateTicket(ticketCode));
    }

    // Annuler un ticket
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            Authentication authentication) {
        ticketService.cancelTicket(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}