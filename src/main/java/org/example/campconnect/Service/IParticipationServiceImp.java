package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Activity;
import org.example.campconnect.Entity.Participation;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.ActivityRepository;
import org.example.campconnect.Repository.ParticipationRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ParticipationCreateRequest;
import org.example.campconnect.dto.ParticipationDTO;
import org.example.campconnect.dto.ParticipationUpdateRequest;
import org.example.campconnect.mapper.ParticipationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IParticipationServiceImp implements IParticipationService {

    private final ParticipationRepository participationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ParticipationMapper participationMapper;

    @Override
    public ParticipationDTO register(ParticipationCreateRequest request, String userEmail) {
        // Vérifier que l'activité existe
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Activité introuvable avec l'ID : " + request.getActivityId()));

        // Récupérer le user connecté
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userEmail));

        // ⭐ Anti-doublon : vérifier qu'il n'est pas déjà inscrit
        if (participationRepository.existsByUser_EmailAndActivity_Id(userEmail, request.getActivityId()))
            throw new IllegalArgumentException("Vous êtes déjà inscrit à cette activité");

        Participation participation = new Participation();
        participation.setParticipationDate(LocalDate.now());
        participation.setStatus(request.getStatus());
        participation.setActivity(activity);
        participation.setUser(user);

        return participationMapper.toDto(participationRepository.save(participation));
    }

    @Override
    public ParticipationDTO getById(Long id) {
        Participation p = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participation introuvable avec l'ID : " + id));
        return participationMapper.toDto(p);
    }

    @Override
    public List<ParticipationDTO> getAll() {
        return participationRepository.findAll()
                .stream()
                .map(participationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationDTO> getMyParticipations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userEmail));
        return participationRepository.findByUser_IdUser(user.getIdUser())
                .stream()
                .map(participationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationDTO> getByActivity(Long activityId) {
        if (!activityRepository.existsById(activityId))
            throw new IllegalArgumentException("Activité introuvable avec l'ID : " + activityId);
        return participationRepository.findByActivity_Id(activityId)
                .stream()
                .map(participationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationDTO updateStatus(Long id, ParticipationUpdateRequest request, String userEmail) {
        Participation participation = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participation introuvable avec l'ID : " + id));

        // Seul le propriétaire peut modifier son statut
        if (!participation.getUser().getEmail().equals(userEmail))
            throw new SecurityException("Vous n'êtes pas autorisé à modifier cette participation");

        participation.setStatus(request.getStatus());
        return participationMapper.toDto(participationRepository.save(participation));
    }

    @Override
    public void cancel(Long id, String userEmail) {
        Participation participation = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participation introuvable avec l'ID : " + id));

        if (!participation.getUser().getEmail().equals(userEmail))
            throw new SecurityException("Vous n'êtes pas autorisé à annuler cette participation");

        participationRepository.deleteById(id);
    }


    @Override
    public void cancelByUser(Long id, String userEmail) {
        Participation p = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Participation introuvable : " + id));

        // Vérifier que c'est bien l'auteur
        if (!p.getUser().getEmail().equals(userEmail))
            throw new SecurityException(
                    "Vous ne pouvez annuler que votre propre participation");

        p.setStatus("CANCELLED");
        participationRepository.save(p);
    }
}