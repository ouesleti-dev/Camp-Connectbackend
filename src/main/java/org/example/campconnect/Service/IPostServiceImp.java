package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Event;
import org.example.campconnect.Entity.Post;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.CommentRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Repository.PostRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.PostCreateRequest;
import org.example.campconnect.dto.PostDTO;
import org.example.campconnect.dto.PostUpdateRequest;
import org.example.campconnect.mapper.PostMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IPostServiceImp implements IPostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Override
    public PostDTO createPost(PostCreateRequest request, String userEmail) {
        // Vérifier que l'événement existe
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Événement introuvable avec l'ID : " + request.getEventId()));

        // Récupérer le user connecté
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userEmail));

        Post post = postMapper.toEntity(request);
        post.setEvent(event);
        post.setUser(user);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post introuvable avec l'ID : " + id));
        return postMapper.toDto(post);
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getPostsByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId))
            throw new IllegalArgumentException("Événement introuvable avec l'ID : " + eventId);
        return postRepository.findByEvent_IdOrderByCreateDateDesc(eventId)
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getMyPosts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userEmail));
        return postRepository.findByUser_IdUser(user.getIdUser())
                .stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO updatePost(Long id, PostUpdateRequest request, String userEmail) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post introuvable avec l'ID : " + id));

        // Vérifier que c'est bien l'auteur ou un admin
        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!post.getUser().getEmail().equals(userEmail) && !isAdmin)
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce post");

        post.setContent(request.getContent());
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public void deletePost(Long id, String userEmail) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post introuvable avec l'ID : " + id));

        // Vérifier que c'est l'auteur ou un admin (le @PreAuthorize dans le controller gère l'admin)
        if (!post.getUser().getEmail().equals(userEmail))
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce post");

        // ⭐ Suppression en cascade : supprimer tous les commentaires du post
        commentRepository.deleteAllByPost_Id(id);

        postRepository.deleteById(id);
    }
}