package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Ticket;
import org.example.campconnect.dto.TicketDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class TicketMapper {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TicketDTO toDto(Ticket ticket, String qrBase64) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                // ⭐ Fix : utilise FORMATTER.format() et non String.format()
                .issueDate(ticket.getIssueDate() != null
                        ? FORMATTER.format(ticket.getIssueDate()) : null)
                .status(ticket.getStatus())
                .eventId(ticket.getEvent() != null
                        ? ticket.getEvent().getId() : null)
                .eventTitle(ticket.getEvent() != null
                        ? ticket.getEvent().getTitle() : null)
                // ⭐ Fix : même chose pour eventDate
                .eventDate(ticket.getEvent() != null
                        && ticket.getEvent().getEventDate() != null
                        ? FORMATTER.format(ticket.getEvent().getEventDate())
                        : null)
                .userId(ticket.getUser() != null
                        ? ticket.getUser().getIdUser() : null)
                .participantFullName(ticket.getUser() != null
                        ? ticket.getUser().getFirstName() + " "
                        + ticket.getUser().getLastName() : null)
                .qrCodeBase64(qrBase64)
                .build();
    }
}