package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Entity.Ticket;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Repository.TicketRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.TicketDTO;
import org.example.campconnect.mapper.TicketMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ITicketServiceImp implements ITicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final QRCodeService qrCodeService;
    private final TicketMapper ticketMapper; // ⭐ injecter le mapper

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public TicketDTO generateTicket(Long eventId, String userEmail) {
        // Vérifier que l'event existe
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Événement introuvable avec l'ID : " + eventId));

        // Vérifier statut event
        if ("COMPLETED".equals(event.getStatus()) ||
                "CANCELLED".equals(event.getStatus()))
            throw new IllegalArgumentException(
                    "Impossible de générer un ticket pour un event "
                            + event.getStatus());

        // Récupérer le user connecté
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable : " + userEmail));

        // Anti-doublon
        if (ticketRepository.existsByUser_EmailAndEvent_Id(userEmail, eventId))
            throw new IllegalArgumentException(
                    "Vous avez déjà un ticket pour cet événement");

        // Générer code unique
        String ticketCode = "TC-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        // Créer le ticket
        Ticket ticket = new Ticket();
        ticket.setTicketCode(ticketCode);
        ticket.setIssueDate(LocalDate.now());
        ticket.setStatus("VALID");
        ticket.setEvent(event);
        ticket.setUser(user);

        Ticket saved = ticketRepository.save(ticket);

        // Générer QR Code
        String qrContent = String.format(
                "TICKET:%s|EVENT:%s|USER:%s|DATE:%s",
                ticketCode,
                event.getTitle(),
                userEmail,
                LocalDate.now().format(FORMATTER)); // ⭐ Fix format date

        String qrBase64 = qrCodeService.generateQRCodeBase64(qrContent);

        return ticketMapper.toDto(saved, qrBase64); // ⭐ utilise le mapper
    }

    @Override
    public List<TicketDTO> getMyTickets(String userEmail) {
        return ticketRepository.findByUser_Email(userEmail)
                .stream()
                .map(t -> ticketMapper.toDto(t, null)) // ⭐ utilise le mapper
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getTicketsByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId))
            throw new IllegalArgumentException(
                    "Événement introuvable avec l'ID : " + eventId);
        return ticketRepository.findByEvent_Id(eventId)
                .stream()
                .map(t -> ticketMapper.toDto(t, null)) // ⭐ utilise le mapper
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO validateTicket(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ticket introuvable avec le code : " + ticketCode));

        if ("CANCELLED".equals(ticket.getStatus()))
            throw new IllegalArgumentException("Ce ticket a été annulé");

        if ("USED".equals(ticket.getStatus()))
            throw new IllegalArgumentException(
                    "Ce ticket a déjà été utilisé");

        // Marquer comme utilisé
        ticket.setStatus("USED");
        Ticket updated = ticketRepository.save(ticket);

        // QR de confirmation
        String qrBase64 = qrCodeService
                .generateQRCodeBase64("VALIDATED:" + ticketCode);

        return ticketMapper.toDto(updated, qrBase64); // ⭐ utilise le mapper
    }

    @Override
    public void cancelTicket(Long id, String userEmail) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ticket introuvable avec l'ID : " + id));

        if (!ticket.getUser().getEmail().equals(userEmail))
            throw new SecurityException(
                    "Vous n'êtes pas autorisé à annuler ce ticket");

        ticket.setStatus("CANCELLED");
        ticketRepository.save(ticket);
    }
}