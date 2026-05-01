package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.RentalRepository;
import org.example.campconnect.Repository.ReviewRepository;
import org.example.campconnect.dto.EquipmentRequestDto;
import org.example.campconnect.dto.EquipmentResponseDto;
import org.example.campconnect.dto.EquipmentStatsDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IEquipmentServiceImp implements IEquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;
    private final ReviewRepository reviewRepository;
    private EquipmentResponseDto toDto(Equipment equipment) {
        EquipmentResponseDto dto = new EquipmentResponseDto();
        dto.setIdEquipement(equipment.getIdEquipement());
        dto.setName(equipment.getName());
        dto.setType(equipment.getType());
        dto.setDescription(equipment.getDescription());
        dto.setOwner(equipment.getOwner());
        dto.setAviability(equipment.getAviability());
        dto.setVerified(equipment.getVerified());
        dto.setState(equipment.getState());
        dto.setPrice(equipment.getPrice());
        dto.setPicture(equipment.getPicture());
        dto.setAverageRating(0.0);
        return dto;
    }
    @Override
    public EquipmentResponseDto createEquipment(EquipmentRequestDto dto, String ownerEmail) {
        Equipment equipment = Equipment.builder()
                .name(dto.getName())
                .type(dto.getType())
                .description(dto.getDescription())
                .owner(ownerEmail)
                .aviability(null)
                .state(State.Not_Reserve)
                .price(dto.getPrice())
                .picture(dto.getPicture())
                .verified(Boolean.FALSE) // ✅ Non vérifié par défaut
                .build();
        return toDto(equipmentRepository.save(equipment));
    }

    // ✅ Corrigé : utilise findByOwner
    @Override
    public List<EquipmentResponseDto> getMyEquipments(String ownerEmail) {
        return equipmentRepository.findByOwner(ownerEmail)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Corrigé : retourne les équipements vérifiés
    @Override
    public List<EquipmentResponseDto> getVerifiedEquipments() {
        return equipmentRepository.findVerifiedEquipmentsWithRating();
    }

    @Override
    public List<EquipmentResponseDto> getUnverifiedEquipments() {
        return equipmentRepository.findByVerifiedFalse()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EquipmentResponseDto verifyEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        equipment.setVerified(Boolean.TRUE);
        return toDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        // ✅ 1. Supprimer les reviews liés à cet équipement
        reviewRepository.deleteByEquipment_IdEquipement(id);
        // ✅ 2. Supprimer les rentals liés
        rentalRepository.deleteByEquipment_IdEquipement(id);
        // ✅ 3. Supprimer l'équipement
        equipmentRepository.deleteById(id);
    }
    @Override
    public EquipmentResponseDto updateEquipment(Long id, EquipmentRequestDto dto, String email) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (email != null && !equipment.getOwner().trim().equalsIgnoreCase(email.trim())) {
            throw new RuntimeException("Not authorized");
        }

        equipment.setName(dto.getName());
        equipment.setType(dto.getType());
        equipment.setDescription(dto.getDescription());
        equipment.setPrice(dto.getPrice());
        equipment.setPicture(dto.getPicture());
        equipment.setVerified(Boolean.FALSE);

        return toDto(equipmentRepository.save(equipment));
    }
    @Override
    public List<EquipmentStatsDto> getEquipmentStats() {
        return equipmentRepository.findEquipmentWithStats();
    }
    @Override
    public List<EquipmentResponseDto> searchEquipments(Type type, State state, Float maxPrice) {
        List<Equipment> results;

        if (type != null && state != null && maxPrice != null) {
            results = equipmentRepository
                    .findByTypeAndStateAndPriceLessThanEqualAndVerifiedTrue(type, state, maxPrice);
        } else if (type != null && maxPrice != null) {
            results = equipmentRepository
                    .findByVerifiedTrueAndTypeAndPriceLessThanEqual(type, maxPrice);
        } else {
            results = equipmentRepository.findByVerifiedTrue();
        }

        return results.stream().map(this::toDto).collect(Collectors.toList());
    }
}
