package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Camping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampingRepository extends JpaRepository<Camping, Long> {

    // Vérifier l'unicité du nom (anti-doublon à la création)
    boolean existsByName(String name);

    // Trouver par statut (OPEN / CLOSED / MAINTENANCE)
    List<Camping> findByStatus(String status);

    // Trouver par code postal
    List<Camping> findByPostalCode(String postalCode);

    // Recherche par nom (pour une future fonctionnalité de recherche)
    Optional<Camping> findByNameIgnoreCase(String name);

    // Compter les events d'un camping
    @Query("SELECT COUNT(e) FROM Event e WHERE e.camping.campingId = :campingId")
    long countEventsByCampingId(Long campingId);

    // Compter les activités d'un camping
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.camping.campingId = :campingId")
    long countActivitiesByCampingId(Long campingId);

    // ══════════════════════════════════════════════════
    // ⭐ VRAIS JOINs JPQL — Fonctionnalité 2
    // ══════════════════════════════════════════════════

    // ⭐ Classement avec GROUP BY + 4 JOINs chaînés
    // JOIN Camping → Event → Activity → Participation (3 JOINs)
    // + SUM, COUNT, GROUP BY
    @Query("""
        SELECT
            c.campingId,
            c.name,
            c.status,
            COUNT(DISTINCT e.id),
            COUNT(DISTINCT a.id),
            COUNT(DISTINCT p.id),
            COALESCE(SUM(e.wasteCollected), 0)
        FROM Camping c
        LEFT JOIN c.events e
        LEFT JOIN e.activities a
        LEFT JOIN a.participations p
        GROUP BY c.campingId, c.name, c.status
        ORDER BY COUNT(DISTINCT p.id) DESC
        """)
    List<Object[]> findCampingRankingRaw();

    // JOIN Camping → Event → Post (2 JOINs chaînés)
    @Query("""
        SELECT COUNT(po)
        FROM Camping c
        JOIN c.events e
        JOIN e.posts po
        WHERE c.campingId = :campingId
        """)
    long countPostsByCamping(@Param("campingId") Long campingId);

    // JOIN Camping → Event → Ticket (2 JOINs chaînés)
    @Query("""
        SELECT COUNT(t)
        FROM Camping c
        JOIN c.events e
        JOIN e.tickets t
        WHERE c.campingId = :campingId
        """)
    long countTicketsByCamping(@Param("campingId") Long campingId);

    // ⭐ AVG taux remplissage avec JOIN Camping → Event → Activity → Participation
    @Query("""
        SELECT
            e.maxParticipants,
            COUNT(p.id)
        FROM Camping c
        JOIN c.events e
        LEFT JOIN e.activities a
        LEFT JOIN a.participations p
        WHERE c.campingId = :campingId
        GROUP BY e.id, e.maxParticipants
        """)
    List<Object[]> getFillRateDataByCamping(@Param("campingId") Long campingId);
}
