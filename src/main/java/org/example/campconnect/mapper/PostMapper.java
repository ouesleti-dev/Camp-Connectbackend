package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Post;
import org.example.campconnect.dto.PostCreateRequest;
import org.example.campconnect.dto.PostDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PostMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PostDTO toDto(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .createDate(post.getCreateDate() != null ? FORMATTER.format(post.getCreateDate()) : null)
                .eventId(post.getEvent() != null ? post.getEvent().getId() : null)
                .eventTitle(post.getEvent() != null ? post.getEvent().getTitle() : null)
                .userId(post.getUser() != null ? post.getUser().getIdUser() : null)
                .authorFullName(post.getUser() != null
                        ? post.getUser().getFirstName() + " " + post.getUser().getLastName()
                        : null)
                .build();
    }

    public Post toEntity(PostCreateRequest req) {
        Post post = new Post();
        post.setContent(req.getContent());
        post.setCreateDate(java.time.LocalDate.now());
        return post;
    }
}