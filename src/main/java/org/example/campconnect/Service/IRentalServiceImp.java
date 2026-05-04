package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.example.campconnect.dto.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IRentalServiceImp implements IRentalService {

    private final RentalRepository      rentalRepository;
    private final EquipmentRepository   equipmentRepository;
    private final MaintenanceRepository maintenanceRepository; // ← AJOUTÉ

    @Override
    public RentalResponseDto requestRental(RentalRequestDto dto, String renterEmail) {
        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.getVerified()) {
            throw new RuntimeException("Equipment not verified");
        }

        // Vérifie pas de conflit de dates avec une location acceptée
        boolean conflict = rentalRepository.existsConflictingRental(
                dto.getEquipmentId(), dto.getStartDate(), dto.getEndDate()
        );
        if (conflict) {
            throw new RuntimeException("Equipment not available for these dates");
        }

        // ← AJOUTÉ : Bloquer si maintenance planifiée sur ces dates
        if (maintenanceRepository.existsOverlappingMaintenance(
                dto.getEquipmentId(),
                dto.getStartDate(), dto.getEndDate())) {
            throw new RuntimeException(
                    "Equipment is under maintenance during this period");
        }

        // Calcul montant total
        long diffMs = dto.getEndDate().getTime() - dto.getStartDate().getTime();
        long days = TimeUnit.MILLISECONDS.toDays(diffMs);
        if (days < 1) days = 1;
        float total = equipment.getPrice() * days;

        Rental rental = Rental.builder()
                .startdate(dto.getStartDate())
                .enddate(dto.getEndDate())
                .totalAmount(total)
                .verified(false)
                .renterEmail(renterEmail)
                .ownerEmail(equipment.getOwner())
                .equipment(equipment)
                .build();

        return toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalResponseDto acceptRental(Long rentalId, String ownerEmail) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.getOwnerEmail().equals(ownerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        rental.setVerified(true);

        return toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalResponseDto> getMyRentals(String renterEmail) {
        return rentalRepository.findByRenterEmail(renterEmail)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RentalResponseDto> getRentalsAsOwner(String ownerEmail) {
        return rentalRepository.findByOwnerEmail(ownerEmail)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private RentalResponseDto toDto(Rental r) {
        RentalResponseDto dto = new RentalResponseDto();
        dto.setRentalId(r.getRentalid());
        dto.setStartDate(r.getStartdate());
        dto.setEndDate(r.getEnddate());
        dto.setTotalAmount(r.getTotalAmount());
        dto.setVerified(r.getVerified());
        dto.setRenterEmail(r.getRenterEmail());
        dto.setOwnerEmail(r.getOwnerEmail());
        dto.setEquipmentId(r.getEquipment().getIdEquipement());
        dto.setEquipmentName(r.getEquipment().getName());
        return dto;
    }

    @Override
    public void deleteRental(Long rentalId, String email) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.getRenterEmail().equalsIgnoreCase(email) &&
                !rental.getOwnerEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Not authorized");
        }

        if (Boolean.TRUE.equals(rental.getVerified())) {
            throw new RuntimeException("Cannot delete an accepted rental");
        }

        rentalRepository.deleteById(rentalId);
    }

    @Override
    public RentalResponseDto updateRental(Long rentalId, RentalRequestDto dto, String email) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.getRenterEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Not authorized");
        }

        if (Boolean.TRUE.equals(rental.getVerified())) {
            throw new RuntimeException("Cannot update an accepted rental");
        }

        Equipment equipment = rental.getEquipment();

        boolean conflict = rentalRepository.existsConflictingRental(
                equipment.getIdEquipement(), dto.getStartDate(), dto.getEndDate()
        );
        if (conflict) {
            throw new RuntimeException("Equipment not available for these dates");
        }

        long diffMs = dto.getEndDate().getTime() - dto.getStartDate().getTime();
        long days = TimeUnit.MILLISECONDS.toDays(diffMs);
        if (days < 1) days = 1;
        float total = equipment.getPrice() * days;

        rental.setStartdate(dto.getStartDate());
        rental.setEnddate(dto.getEndDate());
        rental.setTotalAmount(total);

        return toDto(rentalRepository.save(rental));
    }
}