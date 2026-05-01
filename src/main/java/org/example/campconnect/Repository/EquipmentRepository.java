package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.dto.EquipmentResponseDto;
import org.example.campconnect.dto.EquipmentStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByOwner(String owner);


    List<Equipment> findByVerifiedTrue();


    List<Equipment> findByVerifiedFalse();
    // Dans EquipmentRepository.java — ajoute cette méthode
    @Query("""
    SELECT new org.example.campconnect.dto.EquipmentStatsDto(
        e.idEquipement,
        e.name,
        e.type,
        e.price,
        AVG(r.rating),
        COUNT(DISTINCT r.idreview),
        COUNT(DISTINCT ren.rentalid)
    )
    FROM Equipment e
    LEFT JOIN Review r ON r.equipment.idEquipement = e.idEquipement
    LEFT JOIN Rental ren ON ren.equipment.idEquipement = e.idEquipement
                        AND ren.verified = true
    WHERE e.verified = true
    GROUP BY e.idEquipement, e.name, e.type, e.price
    ORDER BY AVG(r.rating) DESC
    """)
    List<EquipmentStatsDto> findEquipmentWithStats();
    // Keywords JPA impliquant Type + State + Price (3 champs, 2+ tables via reviews)
    List<Equipment> findByTypeAndStateAndPriceLessThanEqualAndVerifiedTrue(
            Type type, State state, Float maxPrice
    );

    // Variante : cherche par type ET prix max (state optionnel)
    List<Equipment> findByVerifiedTrueAndTypeAndPriceLessThanEqual(
            Type type, Float maxPrice
    );

    // Cherche les équipements avec au moins une review — join implicite
// (spring data résout le chemin rentals.equipment)
    List<Equipment> findByVerifiedTrueAndRentals_OwnerEmailAndState(
            String ownerEmail, State state
    );
    @Query("""
    SELECT new org.example.campconnect.dto.EquipmentResponseDto(
        e.idEquipement,
        e.name,
        e.type,
        e.description,
        e.owner,
        e.aviability,
        e.verified,
        e.state,
        e.price,
        e.picture,
        AVG(r.rating)
    )
    FROM Equipment e
    LEFT JOIN Review r ON r.equipment.idEquipement = e.idEquipement
    WHERE e.verified = true
    GROUP BY e.idEquipement, e.name, e.type, e.description,
             e.owner, e.aviability, e.verified, e.state,
             e.price, e.picture
    """)
    List<EquipmentResponseDto> findVerifiedEquipmentsWithRating();
}
