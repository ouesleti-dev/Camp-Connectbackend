package org.example.campconnect;

import org.example.campconnect.Entity.Event;
import org.example.campconnect.Entity.Post;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.CommentRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.Repository.PostRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.Service.IPostServiceImp;
import org.example.campconnect.dto.PostCreateRequest;
import org.example.campconnect.dto.PostDTO;
import org.example.campconnect.mapper.PostMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private IPostServiceImp postService;

    // ── Test 1 : Créer un post avec succès ───────────────────
    @Test
    @DisplayName("Créer un post avec succès")
    void shouldCreatePostSuccessfully() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Nettoyage Forêt");

        User user = new User();
        user.setIdUser(1L);
        user.setEmail("sarra@campconnect.com");

        Post post = new Post();
        post.setId(1L);
        post.setContent("Bonjour tout le monde !");

        PostDTO expectedDto = new PostDTO();
        expectedDto.setId(1L);
        expectedDto.setContent("Bonjour tout le monde !");

        PostCreateRequest request = new PostCreateRequest();
        request.setContent("Bonjour tout le monde !");
        request.setEventId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByEmail("sarra@campconnect.com"))
                .thenReturn(Optional.of(user));
        when(postMapper.toEntity(request)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(expectedDto);

        PostDTO result = postService.createPost(request, "sarra@campconnect.com");

        assertNotNull(result);
        assertEquals("Bonjour tout le monde !", result.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    // ── Test 2 : Event introuvable → exception ───────────────
    @Test
    @DisplayName("Lève une exception si l'événement est introuvable")
    void shouldThrowWhenEventNotFound() {
        PostCreateRequest request = new PostCreateRequest();
        request.setContent("Test post");
        request.setEventId(99L);

        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> postService.createPost(request, "sarra@campconnect.com"));

        verify(postRepository, never()).save(any());
    }

    // ── Test 3 : Supprimer post avec cascade comments ────────
    @Test
    @DisplayName("Supprime un post et tous ses commentaires en cascade")
    void shouldDeletePostWithComments() {
        User user = new User();
        user.setEmail("sarra@campconnect.com");

        Post post = new Post();
        post.setId(1L);
        post.setContent("Post à supprimer");
        post.setUser(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, "sarra@campconnect.com");

        // ⭐ Vérifie que les comments sont supprimés avant le post
        verify(commentRepository, times(1)).deleteAllByPost_Id(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    // ── Test 4 : Supprimer post d'un autre user → exception ──
    @Test
    @DisplayName("Lève une exception si on supprime le post d'un autre")
    void shouldThrowWhenDeletingOtherUserPost() {
        User user = new User();
        user.setEmail("sarra@campconnect.com");

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(SecurityException.class,
                () -> postService.deletePost(1L, "ahmed@campconnect.com"));

        verify(postRepository, never()).deleteById(any());
    }
}