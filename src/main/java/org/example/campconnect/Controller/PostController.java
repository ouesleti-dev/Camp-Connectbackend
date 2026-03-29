package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IPostService;
import org.example.campconnect.dto.PostCreateRequest;
import org.example.campconnect.dto.PostDTO;
import org.example.campconnect.dto.PostUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAll() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // Récupérer les posts du forum d'un événement
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<PostDTO>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(postService.getPostsByEvent(eventId));
    }

    // Mes posts (user connecté)
    @GetMapping("/my")
    public ResponseEntity<List<PostDTO>> getMyPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.getMyPosts(authentication.getName()));
    }

    // Créer un post dans le forum d'un event → user connecté automatiquement associé
    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostCreateRequest request,
                                          Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request, authentication.getName()));
    }

    // Modifier (seulement l'auteur)
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody PostUpdateRequest request,
                                          Authentication authentication) {
        return ResponseEntity.ok(postService.updatePost(id, request, authentication.getName()));
    }

    // Supprimer → supprime tous les comments en cascade ⭐
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        postService.deletePost(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}