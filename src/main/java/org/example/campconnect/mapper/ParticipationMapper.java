package org.example.campconnect.mapper;

import org.example.campconnect.Entity.Participation;
import org.example.campconnect.dto.ParticipationDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ParticipationMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ParticipationDTO toDto(Participation participation) {
        return ParticipationDTO.builder()
                .id(participation.getId())
                .participationDate(participation.getParticipationDate() != null
                        ? FORMATTER.format(participation.getParticipationDate()) : null)
                .status(participation.getStatus())
                .activityId(participation.getActivity() != null ? participation.getActivity().getId() : null)
                .activityName(participation.getActivity() != null ? participation.getActivity().getName() : null)
                .userId(participation.getUser() != null ? participation.getUser().getIdUser() : null)
                .participantFullName(participation.getUser() != null
                        ? participation.getUser().getFirstName() + " " + participation.getUser().getLastName()
                        : null)
                .build();
    }
}