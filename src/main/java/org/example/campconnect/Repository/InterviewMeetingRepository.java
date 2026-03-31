package org.example.campconnect.Repository;

import org.example.campconnect.Entity.InterviewMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface InterviewMeetingRepository extends JpaRepository<InterviewMeeting, Long> {
    Optional<InterviewMeeting> findByInterview_InterviewId(Long interviewId);
}