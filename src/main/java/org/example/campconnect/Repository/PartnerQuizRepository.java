package org.example.campconnect.Repository;
import org.example.campconnect.Entity.PartnerQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PartnerQuizRepository extends JpaRepository<PartnerQuiz, Long> {
    List<PartnerQuiz> findByUser_IdUser(Long userId);
}