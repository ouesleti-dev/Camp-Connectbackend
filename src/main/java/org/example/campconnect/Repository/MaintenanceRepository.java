package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Maintenance;
import org.example.campconnect.Entity.Kind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    // Dernière maintenance terminée pour un équipement
    @Query("""
        SELECT m FROM Maintenance m
        JOIN m.equipment e
        WHERE e.idEquipement = :equipmentId
          AND m.enddate IS NOT NULL
        ORDER BY m.enddate DESC
    """)
    List<Maintenance> findLastByEquipmentId(@Param("equipmentId") Long equipmentId);

    // Toutes les maintenances avec fetch join (equipment + user)
    @Query("""
        SELECT m FROM Maintenance m
        JOIN FETCH m.equipment e
        JOIN FETCH m.user u
        WHERE e.idEquipement = :equipmentId
        ORDER BY m.startdate DESC
    """)
    List<Maintenance> findAllWithDetailsForEquipment(@Param("equipmentId") Long equipmentId);

    // Nombre de maintenances correctives (signe de problèmes répétés)
    @Query("""
        SELECT COUNT(m) FROM Maintenance m
        JOIN m.equipment e
        WHERE e.idEquipement = :equipmentId
          AND m.kind = :kind
    """)
    long countByEquipmentAndKind(@Param("equipmentId") Long equipmentId, @Param("kind") Kind kind);

    // Maintenance en cours (startdate défini, enddate null)
    @Query("""
        SELECT m FROM Maintenance m
        JOIN FETCH m.equipment e
        WHERE e.idEquipement = :equipmentId
          AND m.startdate IS NOT NULL
          AND m.enddate IS NULL
    """)
    Optional<Maintenance> findOngoingMaintenance(@Param("equipmentId") Long equipmentId);
}