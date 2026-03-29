package projectexamen.spring.campconnect.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.PartnerQuestion;

import java.util.List;

@Repository
public interface PartnerQuestionRepository extends JpaRepository<PartnerQuestion, Long> {
    List<PartnerQuestion> findByQuiz_QuizId(Long quizId);
}