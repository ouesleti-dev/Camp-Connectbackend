package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.dto.EquipmentRequestDto;
import org.example.campconnect.dto.EquipmentResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IEquipmentServiceImp implements IEquipmentService {
    private final EquipmentRepository equipmentRepository;
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
        return dto;
    }
    @Override
    public EquipmentResponseDto createEquipment(EquipmentRequestDto dto, String ownerEmail) {
        Equipment equipment = Equipment.builder()
                .name(dto.getName())
                .type(dto.getType())
                .description(dto.getDescription())
                .owner(ownerEmail)
                .aviability(dto.getAviability())
                .state(dto.getState())
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
        return equipmentRepository.findByVerifiedTrue()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
    }
}
