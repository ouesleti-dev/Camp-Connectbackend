package projectexamen.spring.campconnect.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.PartnerQuiz;

import java.util.List;

@Repository
public interface PartnerQuizRepository extends JpaRepository<PartnerQuiz, Long> {
    List<PartnerQuiz> findByUser_IdUser(Long userId);
}
