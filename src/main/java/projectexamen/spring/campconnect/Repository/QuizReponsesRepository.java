package projectexamen.spring.campconnect.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.Quizreponses;

import java.util.List;

@Repository
public interface QuizReponsesRepository extends JpaRepository<Quizreponses, Long> {
    List<Quizreponses> findByQuestion_QuestionId(Long questionId);

    @Query("SELECT COALESCE(SUM(r.grade), 0) FROM Quizreponses r WHERE r.question.quiz.user.idUser = :partnerId")
    Double sumGradesForPartner(@Param("partnerId") Long partnerId);
}