package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Comment;
import org.example.campconnect.Entity.Post;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.CommentRepository;
import org.example.campconnect.Repository.PostRepository;
import org.example.campconnect.Repository.ResponseRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.CommentCreateRequest;
import org.example.campconnect.dto.CommentDTO;
import org.example.campconnect.dto.CommentUpdateRequest;
import org.example.campconnect.mapper.CommentMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ICommentServiceImp implements ICommentService {

    private final CommentRepository commentRepository;
    private final ResponseRepository responseRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDTO addComment(CommentCreateRequest request, String userEmail) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Post introuvable avec l'ID : " + request.getPostId()));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userEmail));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreateDate(LocalDate.now());
        comment.setPost(post);
        comment.setUser(user);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable avec l'ID : " + id));
        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPost(Long postId) {
        if (!postRepository.existsById(postId))
            throw new IllegalArgumentException("Post introuvable avec l'ID : " + postId);
        return commentRepository.findByPost_IdOrderByCreateDateAsc(postId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO updateComment(Long id, CommentUpdateRequest request, String userEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable avec l'ID : " + id));

        if (!comment.getUser().getEmail().equals(userEmail))
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce commentaire");

        comment.setContent(request.getContent());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String userEmail) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire introuvable avec l'ID : " + id));

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!comment.getUser().getEmail().equals(userEmail) && !isAdmin)
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce post");

        // ⭐ Suppression en cascade : supprimer toutes les réponses du commentaire
        responseRepository.deleteAllByComment_Id(id);

        commentRepository.deleteById(id);
    }
}