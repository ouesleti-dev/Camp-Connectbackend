package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    Optional<Story> findByEquipment_IdEquipementAndActiveTrue(Long equipmentId);

    List<Story> findByActiveTrue();

    List<Story> findByUser_EmailAndActiveTrue(String email);

    @Query("SELECT s FROM Story s WHERE s.active = true AND s.expiresAt < :now")
    List<Story> findExpiredStories(@Param("now") Date now);

    // ✅ REMPLACE l'ancienne — vérifie active ET non expirée
    @Query("SELECT COUNT(s) > 0 FROM Story s " +
            "WHERE s.equipment.idEquipement = :equipmentId " +
            "AND s.active = true " +
            "AND s.expiresAt > :now")
    boolean existsActiveAndNotExpiredStoryForEquipment(
            @Param("equipmentId") Long equipmentId,
            @Param("now") Date now
    );

    // ✅ NOUVEAU — pour auto-expirer au moment de publishStory
    @Query("SELECT s FROM Story s " +
            "WHERE s.equipment.idEquipement = :equipmentId " +
            "AND s.active = true " +
            "AND s.expiresAt <= :now")
    List<Story> findExpiredActiveStoriesByEquipment(
            @Param("equipmentId") Long equipmentId,
            @Param("now") Date now
    );
}