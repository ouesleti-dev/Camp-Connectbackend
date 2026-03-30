package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Comment;
import org.example.campconnect.dto.CommentDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CommentDTO toDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createDate(comment.getCreateDate() != null ? FORMATTER.format(comment.getCreateDate()) : null)
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getIdUser() : null)
                .authorFullName(comment.getUser() != null
                        ? comment.getUser().getFirstName() + " " + comment.getUser().getLastName()
                        : null)
                .build();
    }
}