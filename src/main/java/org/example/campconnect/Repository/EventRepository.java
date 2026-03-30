package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCamping_CampingId(Long campingId);

    List<Event> findByStatus(String status);

    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);

    boolean existsByCamping_CampingIdAndTitle(Long campingId, String title);
}