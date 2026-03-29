package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ICommentService;
import org.example.campconnect.dto.CommentCreateRequest;
import org.example.campconnect.dto.CommentDTO;
import org.example.campconnect.dto.CommentUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // Tous les commentaires d'un post (forum)
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    // Commenter un post → user connecté associé automatiquement
    @PostMapping
    public ResponseEntity<CommentDTO> add(@Valid @RequestBody CommentCreateRequest request,
                                          Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(request, authentication.getName()));
    }

    // Modifier (seulement l'auteur)
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody CommentUpdateRequest request,
                                             Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(id, request, authentication.getName()));
    }

    // Supprimer → supprime toutes les responses en cascade ⭐
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}