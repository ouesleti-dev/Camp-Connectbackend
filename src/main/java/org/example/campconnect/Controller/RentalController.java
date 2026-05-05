package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Rental;
import org.example.campconnect.Repository.RentalRepository;
import org.example.campconnect.Service.IRentalService;
import org.example.campconnect.dto.RentalRequestDto;
import org.example.campconnect.dto.RentalResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rental")
@RequiredArgsConstructor
public class RentalController {
    private final IRentalService rentalService;
    private final RentalRepository rentalRepository;

    @PostMapping("/request")
    public ResponseEntity<RentalResponseDto> request(
            @RequestBody RentalRequestDto dto,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.requestRental(dto, authentication.getName()));
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<RentalResponseDto> accept(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.acceptRental(id, authentication.getName()));
    }

    @GetMapping("/my-rentals")
    public ResponseEntity<List<RentalResponseDto>> myRentals(Authentication authentication) {
        return ResponseEntity.ok(rentalService.getMyRentals(authentication.getName()));
    }

    @GetMapping("/received")
    public ResponseEntity<List<RentalResponseDto>> received(Authentication authentication) {
        return ResponseEntity.ok(rentalService.getRentalsAsOwner(authentication.getName()));
    }
    @GetMapping("/reserved-dates/{equipmentId}")
    public ResponseEntity<List<Map<String, Object>>> getReservedDates(
            @PathVariable Long equipmentId) {
        List<Rental> rentals = rentalRepository.findAcceptedRentalsByEquipment(equipmentId);
        List<Map<String, Object>> dates = rentals.stream().map(r -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("startDate", r.getStartdate());
            map.put("endDate", r.getEnddate());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dates);
    }
    // ✅ Supprimer une location
    @DeleteMapping("/{rentalId}")
    public ResponseEntity<Void> deleteRental(
            @PathVariable Long rentalId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            rentalRepository.deleteById(rentalId);
        } else {
            rentalService.deleteRental(rentalId, email);
        }
        return ResponseEntity.noContent().build();
    }

    // ✅ Modifier une location
    @PutMapping("/{rentalId}")
    public ResponseEntity<RentalResponseDto> updateRental(
            @PathVariable Long rentalId,
            @RequestBody RentalRequestDto dto,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(rentalService.updateRental(rentalId, dto, email));
    }
}
