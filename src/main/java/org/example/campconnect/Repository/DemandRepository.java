package org.example.campconnect.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.campconnect.Entity.Equipment;

import java.util.Date;
import java.util.List;

@Repository
public interface DemandRepository extends JpaRepository<Equipment, Long> {

    // Count rentals per equipment in a date range (JPQL with JOIN)
    @Query("""
        SELECT e.idEquipement, COUNT(r.rentalid)
        FROM Equipment e
        LEFT JOIN Rental r ON r.equipment.idEquipement = e.idEquipement
            AND r.verified = true
            AND r.startdate >= :startDate
            AND r.startdate <= :endDate
        WHERE e.verified = true
        GROUP BY e.idEquipement
        """)
    List<Object[]> countRentalsByEquipmentInRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Average rating per equipment (JPQL with JOIN)
    @Query("""
        SELECT e.idEquipement, COALESCE(AVG(rv.rating), 0.0)
        FROM Equipment e
        LEFT JOIN Review rv ON rv.equipment.idEquipement = e.idEquipement
        WHERE e.verified = true
        GROUP BY e.idEquipement
        """)
    List<Object[]> avgRatingByEquipment();

    // All verified equipment with basic info
    @Query("""
        SELECT e.idEquipement, e.name, e.price
        FROM Equipment e
        WHERE e.verified = true
        """)
    List<Object[]> findVerifiedEquipmentBasicInfo();
}