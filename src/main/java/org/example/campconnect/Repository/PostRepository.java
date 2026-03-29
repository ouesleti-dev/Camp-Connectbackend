package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Tous les posts d'un événement (forum de l'event)
    List<Post> findByEvent_IdOrderByCreateDateDesc(Long eventId);

    // Tous les posts d'un utilisateur
    List<Post> findByUser_IdUser(Long userId);

    // Vérifier si un post appartient à un utilisateur
    boolean existsByIdAndUser_Email(Long postId, String email);
}