package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByComment_IdOrderByCreateDateAsc(Long commentId);

    void deleteAllByComment_Id(Long commentId);

    boolean existsByIdAndUser_Email(Long responseId, String email);
}