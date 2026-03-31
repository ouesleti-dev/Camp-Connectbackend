package org.example.campconnect;

import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.RentalRepository;
import org.example.campconnect.Repository.ReviewRepository;
import org.example.campconnect.Service.IEquipmentServiceImp;
import org.example.campconnect.dto.EquipmentRequestDto;
import org.example.campconnect.dto.EquipmentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IEquipmentServiceImpTest {

    @Mock private EquipmentRepository equipmentRepository;
    @Mock private RentalRepository rentalRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private IEquipmentServiceImp equipmentService;

    private Equipment equipment;
    private EquipmentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        equipment = Equipment.builder()
                .idEquipement(1L)
                .name("Tente")
                .type(Type.TENTS)
                .description("Tente 2 places")
                .owner("owner@test.com")
                .verified(false)
                .state(State.Not_Reserve)
                .price(25.0f)
                .picture("pic.jpg")
                .build();

        requestDto = new EquipmentRequestDto();
        requestDto.setName("Tente");
        requestDto.setType(Type.TENTS);
        requestDto.setDescription("Tente 2 places");
        requestDto.setPrice(25.0f);
        requestDto.setPicture("pic.jpg");
    }

    // ───────────────────────────────────────────────
    // createEquipment
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("createEquipment → retourne DTO avec verified=false")
    void createEquipment_success() {
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        EquipmentResponseDto result = equipmentService.createEquipment(requestDto, "owner@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Tente");
        assertThat(result.getVerified()).isFalse();
        assertThat(result.getState()).isEqualTo(State.Not_Reserve);
        verify(equipmentRepository).save(any(Equipment.class));
    }

    // ───────────────────────────────────────────────
    // getMyEquipments
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("getMyEquipments → retourne la liste des équipements du owner")
    void getMyEquipments_success() {
        when(equipmentRepository.findByOwner("owner@test.com")).thenReturn(List.of(equipment));

        List<EquipmentResponseDto> result = equipmentService.getMyEquipments("owner@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwner()).isEqualTo("owner@test.com");
    }

    @Test
    @DisplayName("getMyEquipments → liste vide si aucun équipement")
    void getMyEquipments_empty() {
        when(equipmentRepository.findByOwner("nobody@test.com")).thenReturn(List.of());

        List<EquipmentResponseDto> result = equipmentService.getMyEquipments("nobody@test.com");

        assertThat(result).isEmpty();
    }

    // ───────────────────────────────────────────────
    // getVerifiedEquipments
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("getVerifiedEquipments → retourne uniquement les équipements vérifiés")
    void getVerifiedEquipments_success() {
        equipment.setVerified(true);
        when(equipmentRepository.findByVerifiedTrue()).thenReturn(List.of(equipment));

        List<EquipmentResponseDto> result = equipmentService.getVerifiedEquipments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVerified()).isTrue();
    }

    // ───────────────────────────────────────────────
    // getUnverifiedEquipments
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("getUnverifiedEquipments → retourne les équipements non vérifiés")
    void getUnverifiedEquipments_success() {
        when(equipmentRepository.findByVerifiedFalse()).thenReturn(List.of(equipment));

        List<EquipmentResponseDto> result = equipmentService.getUnverifiedEquipments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVerified()).isFalse();
    }

    // ───────────────────────────────────────────────
    // verifyEquipment
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("verifyEquipment → passe verified à true")
    void verifyEquipment_success() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        equipment.setVerified(true);
        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        EquipmentResponseDto result = equipmentService.verifyEquipment(1L);

        assertThat(result.getVerified()).isTrue();
        verify(equipmentRepository).save(equipment);
    }

    @Test
    @DisplayName("verifyEquipment → lève RuntimeException si équipement introuvable")
    void verifyEquipment_notFound() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentService.verifyEquipment(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not found");
    }

    // ───────────────────────────────────────────────
    // deleteEquipment
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("deleteEquipment → supprime reviews, rentals puis équipement")
    void deleteEquipment_success() {
        doNothing().when(reviewRepository).deleteByEquipment_IdEquipement(1L);
        doNothing().when(rentalRepository).deleteByEquipment_IdEquipement(1L);
        doNothing().when(equipmentRepository).deleteById(1L);

        equipmentService.deleteEquipment(1L);

        verify(reviewRepository).deleteByEquipment_IdEquipement(1L);
        verify(rentalRepository).deleteByEquipment_IdEquipement(1L);
        verify(equipmentRepository).deleteById(1L);
    }

    // ───────────────────────────────────────────────
    // updateEquipment
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("updateEquipment → met à jour les champs et repasse verified=false")
    void updateEquipment_success() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        requestDto.setName("Tente XL");
        EquipmentResponseDto result = equipmentService.updateEquipment(1L, requestDto, "owner@test.com");

        assertThat(result).isNotNull();
        verify(equipmentRepository).save(equipment);
        assertThat(equipment.getVerified()).isFalse();
    }

    @Test
    @DisplayName("updateEquipment → lève RuntimeException si non autorisé")
    void updateEquipment_notAuthorized() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertThatThrownBy(() -> equipmentService.updateEquipment(1L, requestDto, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not authorized");
    }

    @Test
    @DisplayName("updateEquipment → lève RuntimeException si équipement introuvable")
    void updateEquipment_notFound() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentService.updateEquipment(99L, requestDto, "owner@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not found");
    }

    @Test
    @DisplayName("updateEquipment → admin (email=null) peut modifier sans vérification owner")
    void updateEquipment_adminCanUpdate() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        EquipmentResponseDto result = equipmentService.updateEquipment(1L, requestDto, null);

        assertThat(result).isNotNull();
        verify(equipmentRepository).save(equipment);
    }
}