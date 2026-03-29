package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Camping;
import org.example.campconnect.Repository.CampingRepository;
import org.example.campconnect.Repository.EventRepository;
import org.example.campconnect.dto.CampingCreateRequest;
import org.example.campconnect.dto.CampingDTO;
import org.example.campconnect.dto.CampingUpdateRequest;
import org.example.campconnect.mapper.CampingMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ICampingServiceImp implements ICampingService {

    private final CampingRepository campingRepository;
    private final EventRepository eventRepository;
    private final CampingMapper campingMapper;

    @Override
    public CampingDTO createCamping(CampingCreateRequest request) {
        // ⭐ Anti-doublon : vérifier qu'aucun camping n'a déjà ce nom
        if (campingRepository.existsByName(request.getName()))
            throw new IllegalArgumentException(
                    "Un camping avec le nom '" + request.getName() + "' existe déjà");

        Camping camping = campingMapper.toEntity(request);
        return campingMapper.toDto(campingRepository.save(camping));
    }

    @Override
    public CampingDTO getCampingById(Long id) {
        Camping camping = campingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Camping introuvable avec l'ID : " + id));
        CampingDTO dto = campingMapper.toDto(camping);
        dto.setTotalEvents((int) campingRepository.countEventsByCampingId(id));
        dto.setTotalActivities((int) campingRepository.countActivitiesByCampingId(id));
        return dto;
    }

    @Override
    public List<CampingDTO> getAllCampings() {
        return campingRepository.findAll()
                .stream()
                .map(camping -> {
                    CampingDTO dto = campingMapper.toDto(camping);
                    // ⭐ Comptage dans la session via requête JPQL
                    dto.setTotalEvents((int) campingRepository
                            .countEventsByCampingId(camping.getCampingId()));
                    dto.setTotalActivities((int) campingRepository
                            .countActivitiesByCampingId(camping.getCampingId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CampingDTO> getCampingsByStatus(String status) {
        return campingRepository.findByStatus(status)
                .stream()
                .map(campingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CampingDTO updateCamping(Long id, CampingUpdateRequest request) {
        Camping camping = campingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Camping introuvable avec l'ID : " + id));

        // Mise à jour uniquement des champs fournis (patch partiel)
        if (request.getName() != null && !request.getName().isBlank()) {
            // Vérifier l'unicité du nouveau nom (sauf si c'est le même camping)
            if (!request.getName().equalsIgnoreCase(camping.getName())
                    && campingRepository.existsByName(request.getName()))
                throw new IllegalArgumentException(
                        "Un camping avec le nom '" + request.getName() + "' existe déjà");
            camping.setName(request.getName());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank())
            camping.setAddress(request.getAddress());
        if (request.getDescription() != null)
            camping.setDescription(request.getDescription());
        if (request.getPostalCode() != null && !request.getPostalCode().isBlank())
            camping.setPostalCode(request.getPostalCode());
        if (request.getStatus() != null && !request.getStatus().isBlank())
            camping.setStatus(request.getStatus());

        return campingMapper.toDto(campingRepository.save(camping));
    }

    @Override
    public void deleteCamping(Long id) {
        Camping camping = campingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Camping introuvable avec l'ID : " + id));

        // ⭐ Vérification logique : on ne supprime pas un camping qui a encore des events actifs
        boolean hasActiveEvents = eventRepository.findByCamping_CampingId(id)
                .stream()
                .anyMatch(e -> "PLANNED".equals(e.getStatus()) || "ONGOING".equals(e.getStatus()));

        if (hasActiveEvents)
            throw new IllegalArgumentException(
                    "Impossible de supprimer ce camping : il possède encore des événements PLANNED ou ONGOING. "
                            + "Veuillez d'abord annuler ou terminer ces événements.");

        campingRepository.deleteById(id);
    }
}