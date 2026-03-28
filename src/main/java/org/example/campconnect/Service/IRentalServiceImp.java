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

    private final RentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;

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

        // verified = true → accepté
        rental.setVerified(true);

        // Marque équipement comme Reserve
        Equipment equipment = rental.getEquipment();
        equipment.setState(State.Reserve);
        equipmentRepository.save(equipment);

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
}