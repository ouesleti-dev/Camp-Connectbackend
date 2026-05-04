package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser_Email(String email);

    List<Ticket> findByEvent_Id(Long eventId);

    Optional<Ticket> findByTicketCode(String ticketCode);

    boolean existsByUser_EmailAndEvent_Id(String email, Long eventId);
}