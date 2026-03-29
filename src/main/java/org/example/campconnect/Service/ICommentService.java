package org.example.campconnect.Service;

import org.example.campconnect.dto.CommentCreateRequest;
import org.example.campconnect.dto.CommentDTO;
import org.example.campconnect.dto.CommentUpdateRequest;

import java.util.List;

public interface ICommentService {

    CommentDTO addComment(CommentCreateRequest request, String userEmail);

    CommentDTO getCommentById(Long id);

    List<CommentDTO> getCommentsByPost(Long postId);

    CommentDTO updateComment(Long id, CommentUpdateRequest request, String userEmail);

    // Supprime le comment ET toutes ses responses ⭐
    void deleteComment(Long id, String userEmail);
}