package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IRentalService;
import org.example.campconnect.dto.RentalRequestDto;
import org.example.campconnect.dto.RentalResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental")
@RequiredArgsConstructor
public class RentalController {
    private final IRentalService rentalService;

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
}
