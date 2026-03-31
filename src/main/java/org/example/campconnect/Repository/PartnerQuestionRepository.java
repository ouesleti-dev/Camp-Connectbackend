package org.example.campconnect.Repository;

import org.example.campconnect.Entity.PartnerQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PartnerQuestionRepository extends JpaRepository<PartnerQuestion, Long> {
    List<PartnerQuestion> findByQuiz_QuizId(Long quizId);
}