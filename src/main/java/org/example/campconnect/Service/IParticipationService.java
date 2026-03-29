package org.example.campconnect.Service;

import org.example.campconnect.dto.ParticipationCreateRequest;
import org.example.campconnect.dto.ParticipationDTO;
import org.example.campconnect.dto.ParticipationUpdateRequest;

import java.util.List;

public interface IParticipationService {

    ParticipationDTO register(ParticipationCreateRequest request, String userEmail);

    ParticipationDTO getById(Long id);

    List<ParticipationDTO> getAll();

    List<ParticipationDTO> getMyParticipations(String userEmail);

    List<ParticipationDTO> getByActivity(Long activityId);

    ParticipationDTO updateStatus(Long id, ParticipationUpdateRequest request, String userEmail);

    void cancel(Long id, String userEmail);
}