package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByUser_IdUser(Long userId);

    List<Participation> findByActivity_Id(Long activityId);

    List<Participation> findByStatus(String status);

    // Vérifier si l'utilisateur est déjà inscrit à cette activité (anti-doublon)
    boolean existsByUser_EmailAndActivity_Id(String email, Long activityId);
}