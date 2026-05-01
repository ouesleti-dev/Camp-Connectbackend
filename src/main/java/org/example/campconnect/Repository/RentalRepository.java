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
    // Dans RentalRepository.java — ajoute cette méthode
    @Query("SELECT r FROM Rental r WHERE r.verified = true AND r.enddate < :now")
    List<Rental> findExpiredAcceptedRentals(@Param("now") Date now);

}
