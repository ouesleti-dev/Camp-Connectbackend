package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Response;
import org.example.campconnect.dto.ResponseDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ResponseMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ResponseDTO toDto(Response response) {
        return ResponseDTO.builder()
                .id(response.getId())
                .content(response.getContent())
                .createDate(response.getCreateDate() != null ? FORMATTER.format(response.getCreateDate()) : null)
                .commentId(response.getComment() != null ? response.getComment().getId() : null)
                .userId(response.getUser() != null ? response.getUser().getIdUser() : null)
                .authorFullName(response.getUser() != null
                        ? response.getUser().getFirstName() + " " + response.getUser().getLastName()
                        : null)
                .build();
    }
}