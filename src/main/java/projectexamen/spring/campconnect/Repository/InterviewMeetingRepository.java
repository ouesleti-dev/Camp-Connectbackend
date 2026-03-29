package projectexamen.spring.campconnect.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.InterviewMeeting;

import java.util.Optional;

@Repository
public interface InterviewMeetingRepository extends JpaRepository<InterviewMeeting, Long> {
    Optional<InterviewMeeting> findByInterview_InterviewId(Long interviewId);
}