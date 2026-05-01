package org.example.campconnect.Service;

import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.dto.EquipmentRequestDto;
import org.example.campconnect.dto.EquipmentResponseDto;
import org.example.campconnect.dto.EquipmentStatsDto;

import java.util.List;

public interface IEquipmentService {
    EquipmentResponseDto createEquipment(EquipmentRequestDto dto, String ownerEmail);
    List<EquipmentResponseDto> getMyEquipments(String ownerEmail);
    List<EquipmentResponseDto> getVerifiedEquipments();
    List<EquipmentResponseDto> getUnverifiedEquipments();
    EquipmentResponseDto verifyEquipment(Long id);
    void deleteEquipment(Long id);
    EquipmentResponseDto updateEquipment(Long id, EquipmentRequestDto dto, String email);
    List<EquipmentStatsDto> getEquipmentStats();
    List<EquipmentResponseDto> searchEquipments(Type type, State state, Float maxPrice);
}
