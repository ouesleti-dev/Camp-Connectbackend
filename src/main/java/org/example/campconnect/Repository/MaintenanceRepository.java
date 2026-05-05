package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Maintenance;
import org.example.campconnect.Entity.Kind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository
        extends JpaRepository<Maintenance, Long> {

    // ── TES MÉTHODES EXISTANTES ───────────────────────────────────

    @Query("""
        SELECT m FROM Maintenance m
        JOIN m.equipment e
        WHERE e.idEquipement = :equipmentId
          AND m.enddate IS NOT NULL
        ORDER BY m.enddate DESC
    """)
    List<Maintenance> findLastByEquipmentId(
            @Param("equipmentId") Long equipmentId);

    @Query("""
        SELECT m FROM Maintenance m
        JOIN FETCH m.equipment e
        JOIN FETCH m.user u
        WHERE e.idEquipement = :equipmentId
        ORDER BY m.startdate DESC
    """)
    List<Maintenance> findAllWithDetailsForEquipment(
            @Param("equipmentId") Long equipmentId);

    @Query("""
        SELECT COUNT(m) FROM Maintenance m
        JOIN m.equipment e
        WHERE e.idEquipement = :equipmentId
          AND m.kind = :kind
    """)
    long countByEquipmentAndKind(
            @Param("equipmentId") Long equipmentId,
            @Param("kind") Kind kind);

    @Query("""
        SELECT m FROM Maintenance m
        JOIN FETCH m.equipment e
        WHERE e.idEquipement = :equipmentId
          AND m.startdate IS NOT NULL
          AND m.enddate IS NULL
    """)
    Optional<Maintenance> findOngoingMaintenance(
            @Param("equipmentId") Long equipmentId);

    // ── NOUVELLES MÉTHODES POUR LA FEATURE ───────────────────────

    // Toutes les maintenances d'un équipement (pour l'historique)
    @Query("""
        SELECT m FROM Maintenance m
        WHERE m.equipment.idEquipement = :equipmentId
        ORDER BY m.startdate DESC
    """)
    List<Maintenance> findByEquipment_IdEquipement(
            @Param("equipmentId") Long equipmentId);

    // Vérifie si une période chevauche une maintenance existante
    @Query("""
        SELECT COUNT(m) > 0 FROM Maintenance m
        WHERE m.equipment.idEquipement = :equipmentId
          AND m.startdate < :endDate
          AND m.enddate   > :startDate
    """)
    boolean existsOverlappingMaintenance(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate")   Date startDate,
            @Param("endDate")     Date endDate);
}