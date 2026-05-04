package org.example.campconnect.Service;


import org.example.campconnect.dto.TicketDTO;
import java.util.List;

public interface ITicketService {

    // ⭐ Générer un ticket quand user s'inscrit à un event
    TicketDTO generateTicket(Long eventId, String userEmail);

    // Mes tickets (user connecté)
    List<TicketDTO> getMyTickets(String userEmail);

    // Tous les tickets d'un event (pour l'organisateur)
    List<TicketDTO> getTicketsByEvent(Long eventId);

    // ⭐ Valider un ticket par son code (scan QR)
    TicketDTO validateTicket(String ticketCode);

    // Annuler un ticket
    void cancelTicket(Long id, String userEmail);
}