package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByUser_IdUser(Long userId);

    List<Participation> findByActivity_Id(Long activityId);

    List<Participation> findByStatus(String status);

    // Vérifier si l'utilisateur est déjà inscrit à cette activité (anti-doublon)
    boolean existsByUser_EmailAndActivity_Id(String email, Long activityId);



    // ⭐ JOIN Participation → Activity → Event + sous-requête user stats
    @Query("""
    SELECT
        p.user.email,
        p.status,
        a.difficulty,
        MONTH(e.eventDate),
        (SELECT COUNT(p2) FROM Participation p2
         WHERE p2.user.email = p.user.email),
        (SELECT COUNT(p3) FROM Participation p3
         WHERE p3.user.email = p.user.email
         AND p3.status = 'CANCELLED')
    FROM Participation p
    JOIN p.activity a
    JOIN a.event e
    WHERE e.eventDate IS NOT NULL
    """)
    List<Object[]> findDropoutFeaturesRaw();
}