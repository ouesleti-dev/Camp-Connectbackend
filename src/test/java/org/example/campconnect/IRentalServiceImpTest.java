package org.example.campconnect;


import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.Rental;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.RentalRepository;
import org.example.campconnect.Service.IRentalServiceImp;
import org.example.campconnect.dto.RentalRequestDto;
import org.example.campconnect.dto.RentalResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IRentalServiceImpTest {

    @Mock private RentalRepository rentalRepository;
    @Mock private EquipmentRepository equipmentRepository;

    @InjectMocks private IRentalServiceImp rentalService;

    private Equipment equipment;
    private Rental rental;
    private RentalRequestDto requestDto;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        startDate = new Date(System.currentTimeMillis());
        endDate   = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3));

        equipment = Equipment.builder()
                .idEquipement(1L)
                .name("Tente")
                .type(Type.TENTS)
                .owner("owner@test.com")
                .verified(true)
                .state(State.Not_Reserve)
                .price(20.0f)
                .build();

        rental = Rental.builder()
                .rentalid(10L)
                .startdate(startDate)
                .enddate(endDate)
                .totalAmount(60.0f)
                .verified(false)
                .renterEmail("renter@test.com")
                .ownerEmail("owner@test.com")
                .equipment(equipment)
                .build();

        requestDto = new RentalRequestDto();
        requestDto.setEquipmentId(1L);
        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);
    }

    // ───────────────────────────────────────────────
    // requestRental
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("requestRental → crée une location avec montant calculé")
    void requestRental_success() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(rentalRepository.existsConflictingRental(any(), any(), any())).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        RentalResponseDto result = rentalService.requestRental(requestDto, "renter@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getRenterEmail()).isEqualTo("renter@test.com");
        assertThat(result.getTotalAmount()).isGreaterThan(0);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    @DisplayName("requestRental → lève exception si équipement introuvable")
    void requestRental_equipmentNotFound() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.requestRental(requestDto, "renter@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not found");
    }

    @Test
    @DisplayName("requestRental → lève exception si équipement non vérifié")
    void requestRental_equipmentNotVerified() {
        equipment.setVerified(false);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertThatThrownBy(() -> rentalService.requestRental(requestDto, "renter@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not verified");
    }

    @Test
    @DisplayName("requestRental → lève exception si conflit de dates")
    void requestRental_dateConflict() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(rentalRepository.existsConflictingRental(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> rentalService.requestRental(requestDto, "renter@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not available for these dates");
    }

    // ───────────────────────────────────────────────
    // acceptRental
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("acceptRental → passe verified à true")
    void acceptRental_success() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        rental.setVerified(true);
        when(rentalRepository.save(rental)).thenReturn(rental);

        RentalResponseDto result = rentalService.acceptRental(10L, "owner@test.com");

        assertThat(result.getVerified()).isTrue();
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("acceptRental → lève exception si non autorisé")
    void acceptRental_unauthorized() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.acceptRental(10L, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unauthorized");
    }

    @Test
    @DisplayName("acceptRental → lève exception si location introuvable")
    void acceptRental_notFound() {
        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.acceptRental(99L, "owner@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Rental not found");
    }

    // ───────────────────────────────────────────────
    // getMyRentals / getRentalsAsOwner
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("getMyRentals → retourne les locations du renter")
    void getMyRentals_success() {
        when(rentalRepository.findByRenterEmail("renter@test.com")).thenReturn(List.of(rental));

        List<RentalResponseDto> result = rentalService.getMyRentals("renter@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRenterEmail()).isEqualTo("renter@test.com");
    }

    @Test
    @DisplayName("getRentalsAsOwner → retourne les locations reçues par le owner")
    void getRentalsAsOwner_success() {
        when(rentalRepository.findByOwnerEmail("owner@test.com")).thenReturn(List.of(rental));

        List<RentalResponseDto> result = rentalService.getRentalsAsOwner("owner@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwnerEmail()).isEqualTo("owner@test.com");
    }

    // ───────────────────────────────────────────────
    // deleteRental
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("deleteRental → renter peut supprimer une location non acceptée")
    void deleteRental_renterSuccess() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        doNothing().when(rentalRepository).deleteById(10L);

        rentalService.deleteRental(10L, "renter@test.com");

        verify(rentalRepository).deleteById(10L);
    }

    @Test
    @DisplayName("deleteRental → owner peut supprimer une location non acceptée")
    void deleteRental_ownerSuccess() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        doNothing().when(rentalRepository).deleteById(10L);

        rentalService.deleteRental(10L, "owner@test.com");

        verify(rentalRepository).deleteById(10L);
    }

    @Test
    @DisplayName("deleteRental → lève exception si location déjà acceptée")
    void deleteRental_alreadyAccepted() {
        rental.setVerified(true);
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.deleteRental(10L, "renter@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot delete an accepted rental");
    }

    @Test
    @DisplayName("deleteRental → lève exception si non autorisé")
    void deleteRental_notAuthorized() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.deleteRental(10L, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not authorized");
    }

    // ───────────────────────────────────────────────
    // updateRental
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("updateRental → met à jour les dates et recalcule le montant")
    void updateRental_success() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        when(rentalRepository.existsConflictingRental(any(), any(), any())).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        RentalResponseDto result = rentalService.updateRental(10L, requestDto, "renter@test.com");

        assertThat(result).isNotNull();
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("updateRental → lève exception si non autorisé")
    void updateRental_notAuthorized() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.updateRental(10L, requestDto, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not authorized");
    }

    @Test
    @DisplayName("updateRental → lève exception si location déjà acceptée")
    void updateRental_alreadyAccepted() {
        rental.setVerified(true);
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.updateRental(10L, requestDto, "renter@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot update an accepted rental");
    }

    @Test
    @DisplayName("updateRental → lève exception si conflit de dates")
    void updateRental_dateConflict() {
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        when(rentalRepository.existsConflictingRental(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> rentalService.updateRental(10L, requestDto, "renter@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not available for these dates");
    }
}