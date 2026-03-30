package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost_IdOrderByCreateDateAsc(Long postId);

    void deleteAllByPost_Id(Long postId);

    boolean existsByIdAndUser_Email(Long commentId, String email);
}