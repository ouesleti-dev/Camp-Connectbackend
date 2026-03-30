package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Activités d'un événement
    List<Activity> findByEvent_Id(Long eventId);

    // Activités d'un camping
    List<Activity> findByCamping_CampingId(Long campingId);

    // Filtrer par difficulté
    List<Activity> findByDifficulty(String difficulty);

    // Vérifier doublon nom dans le même event
    boolean existsByNameAndEvent_Id(String name, Long eventId);

    // Vérifier doublon nom dans le même camping
    boolean existsByNameAndCamping_CampingId(String name, Long campingId);

    @Query("SELECT COUNT(p) FROM Participation p WHERE p.activity.id = :activityId")
    long countParticipationsByActivityId(Long activityId);
}