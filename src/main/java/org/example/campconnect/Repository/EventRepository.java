package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCamping_CampingId(Long campingId);

    List<Event> findByStatus(String status);

    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);

    boolean existsByCamping_CampingIdAndTitle(Long campingId, String title);


    // ══════════════════════════════════════════════════
    // ⭐ VRAIS JOINs JPQL — Fonctionnalité 1
    // ══════════════════════════════════════════════════

    // JOIN Event → Activity (1 JOIN)
    @Query("""
        SELECT COUNT(a)
        FROM Event e
        JOIN e.activities a
        WHERE e.id = :eventId
        """)
    long countActivitiesByEvent(@Param("eventId") Long eventId);

    // JOIN Event → Activity filtré par difficulté (1 JOIN + condition)
    @Query("""
        SELECT COUNT(a)
        FROM Event e
        JOIN e.activities a
        WHERE e.id = :eventId
        AND a.difficulty = :difficulty
        """)
    long countActivitiesByEventAndDifficulty(
            @Param("eventId") Long eventId,
            @Param("difficulty") String difficulty);

    // JOIN Event → Activity → Participation (2 JOINs chaînés)
    @Query("""
        SELECT COUNT(p)
        FROM Event e
        JOIN e.activities a
        JOIN a.participations p
        WHERE e.id = :eventId
        """)
    long countParticipationsByEvent(@Param("eventId") Long eventId);

    // JOIN Event → Post (1 JOIN)
    @Query("""
        SELECT COUNT(po)
        FROM Event e
        JOIN e.posts po
        WHERE e.id = :eventId
        """)
    long countPostsByEvent(@Param("eventId") Long eventId);

    // JOIN Event → Post → Comment (2 JOINs chaînés)
    @Query("""
        SELECT COUNT(c)
        FROM Event e
        JOIN e.posts po
        JOIN po.comments c
        WHERE e.id = :eventId
        """)
    long countCommentsByEvent(@Param("eventId") Long eventId);

    // JOIN Event → Ticket filtré par statut (1 JOIN + condition)
    @Query("""
        SELECT COUNT(t)
        FROM Event e
        JOIN e.tickets t
        WHERE e.id = :eventId
        AND t.status = :status
        """)
    long countTicketsByEventAndStatus(
            @Param("eventId") Long eventId,
            @Param("status") String status);

    // ⭐ Requête combinée — tout en une seule requête avec SELECT multiple
    // JOIN sur Event → Activity → Participation + Event → Post → Comment + Event → Ticket
    @Query("""
        SELECT
            COUNT(DISTINCT a.id),
            COUNT(DISTINCT p.id),
            COUNT(DISTINCT po.id),
            COUNT(DISTINCT c.id),
            COUNT(DISTINCT t.id)
        FROM Event e
        LEFT JOIN e.activities a
        LEFT JOIN a.participations p
        LEFT JOIN e.posts po
        LEFT JOIN po.comments c
        LEFT JOIN e.tickets t
        WHERE e.id = :eventId
        """)
    Object[] getEventStatsInOneQuery(@Param("eventId") Long eventId);
}















