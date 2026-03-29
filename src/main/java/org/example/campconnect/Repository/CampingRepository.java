package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Camping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
