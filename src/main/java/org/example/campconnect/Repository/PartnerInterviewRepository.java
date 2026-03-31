package org.example.campconnect.Repository;
import org.example.campconnect.Entity.PartnerInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PartnerInterviewRepository extends JpaRepository<PartnerInterview, Long> {
    List<PartnerInterview> findByUser_IdUser(Long userId);
}
