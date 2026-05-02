package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ── Méthodes existantes ─────────────────────────────────────────────────

    List<Review> findByEquipment_IdEquipement(Long equipmentId);

    Optional<Review> findByUser_EmailAndEquipment_IdEquipement(String email, Long equipmentId);

    void deleteByEquipment_IdEquipement(Long equipmentId);

    // ── Nouvelles méthodes ──────────────────────────────────────────────────

    // Tous les reviews avec jointure sur equipment et user
    @Query("""
        SELECT rv FROM Review rv
        JOIN rv.equipment e
        JOIN rv.user u
        WHERE e.idEquipement = :equipmentId
    """)
    List<Review> findAllByEquipmentId(@Param("equipmentId") Long equipmentId);

    // Moyenne des ratings
    @Query("""
        SELECT AVG(rv.rating) FROM Review rv
        JOIN rv.equipment e
        WHERE e.idEquipement = :equipmentId
    """)
    Double findAverageRatingByEquipmentId(@Param("equipmentId") Long equipmentId);

    // Nombre de mauvais ratings (≤ 2)
    @Query("""
        SELECT COUNT(rv) FROM Review rv
        JOIN rv.equipment e
        WHERE e.idEquipement = :equipmentId
          AND rv.rating <= :threshold
    """)
    long countLowRatings(@Param("equipmentId") Long equipmentId, @Param("threshold") int threshold);

    // Distribution des ratings (pour stats UI)
    @Query("""
        SELECT rv.rating, COUNT(rv) FROM Review rv
        JOIN rv.equipment e
        WHERE e.idEquipement = :equipmentId
        GROUP BY rv.rating
        ORDER BY rv.rating ASC
    """)
    List<Object[]> getRatingDistribution(@Param("equipmentId") Long equipmentId);

    // Reviews avec fetch join (évite le N+1)
    @Query("""
        SELECT rv FROM Review rv
        JOIN FETCH rv.user u
        JOIN FETCH rv.equipment e
        WHERE e.idEquipement = :equipmentId
        ORDER BY rv.rating ASC
    """)
    List<Review> findAllWithUserAndEquipment(@Param("equipmentId") Long equipmentId);
}