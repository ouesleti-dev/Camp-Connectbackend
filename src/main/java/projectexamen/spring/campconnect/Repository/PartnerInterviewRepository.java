package projectexamen.spring.campconnect.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.PartnerInterview;

import java.util.List;

@Repository
public interface PartnerInterviewRepository extends JpaRepository<PartnerInterview, Long> {
    List<PartnerInterview> findByUser_IdUser(Long userId);
}
