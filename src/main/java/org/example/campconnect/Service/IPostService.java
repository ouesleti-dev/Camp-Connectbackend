package org.example.campconnect.Service;

import org.example.campconnect.dto.PostCreateRequest;
import org.example.campconnect.dto.PostDTO;
import org.example.campconnect.dto.PostUpdateRequest;

import java.util.List;

public interface IPostService {

    PostDTO createPost(PostCreateRequest request, String userEmail);

    PostDTO getPostById(Long id);

    List<PostDTO> getAllPosts();

    List<PostDTO> getPostsByEvent(Long eventId);

    List<PostDTO> getMyPosts(String userEmail);

    PostDTO updatePost(Long id, PostUpdateRequest request, String userEmail);

    void deletePost(Long id, String userEmail);
}