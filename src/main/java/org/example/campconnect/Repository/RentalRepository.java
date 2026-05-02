package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // ── Méthodes existantes ─────────────────────────────────────────────────

    void deleteByEquipment_IdEquipement(Long equipmentId);

    List<Rental> findByRenterEmail(String renterEmail);

    List<Rental> findByOwnerEmail(String ownerEmail);

    @Query("SELECT COUNT(r) > 0 FROM Rental r JOIN r.equipment e WHERE e.idEquipement = :equipmentId " +
            "AND r.verified = true " +
            "AND r.startdate <= :endDate AND r.enddate >= :startDate")
    boolean existsConflictingRental(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT r FROM Rental r WHERE r.equipment.idEquipement = :equipmentId AND r.verified = true")
    List<Rental> findAcceptedRentalsByEquipment(Long equipmentId);

    @Query("SELECT r FROM Rental r WHERE r.verified = true AND r.enddate < :now")
    List<Rental> findExpiredAcceptedRentals(@Param("now") Date now);

    // ── Nouvelles méthodes ──────────────────────────────────────────────────

    // Tous les rentals d'un équipement avec jointure explicite
    @Query("""
        SELECT r FROM Rental r
        JOIN r.equipment e
        WHERE e.idEquipement = :equipmentId
    """)
    List<Rental> findAllByEquipmentId(@Param("equipmentId") Long equipmentId);

    // Rentals actifs (enddate >= aujourd'hui)
    @Query("""
        SELECT COUNT(r) FROM Rental r
        JOIN r.equipment e
        WHERE e.idEquipement = :equipmentId
          AND r.enddate >= :now
    """)
    long countActiveRentals(@Param("equipmentId") Long equipmentId, @Param("now") Date now);

    // Rentals dans une période donnée (ex: 90 derniers jours)
    @Query("""
        SELECT COUNT(r) FROM Rental r
        JOIN r.equipment e
        WHERE e.idEquipement = :equipmentId
          AND r.startdate >= :since
    """)
    long countRentalsInPeriod(@Param("equipmentId") Long equipmentId, @Param("since") Date since);

    // Durée totale de location en jours (pour calculer l'usure)
    @Query("""
        SELECT SUM(DATEDIFF(r.enddate, r.startdate)) FROM Rental r
        JOIN r.equipment e
        WHERE e.idEquipement = :equipmentId
          AND r.verified = true
    """)
    Long sumRentalDaysForEquipment(@Param("equipmentId") Long equipmentId);

    // Rentals avec infos équipement (fetch join)
    @Query("""
        SELECT r FROM Rental r
        JOIN FETCH r.equipment e
        WHERE e.idEquipement = :equipmentId
        ORDER BY r.startdate DESC
    """)
    List<Rental> findAllWithEquipmentByEquipmentId(@Param("equipmentId") Long equipmentId);
}