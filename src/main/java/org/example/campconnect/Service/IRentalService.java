package org.example.campconnect.Service;

import org.example.campconnect.dto.RentalRequestDto;
import org.example.campconnect.dto.RentalResponseDto;

import java.util.List;

public interface IRentalService {
    RentalResponseDto requestRental(RentalRequestDto dto, String renterEmail);
    RentalResponseDto acceptRental(Long rentalId, String ownerEmail);
    List<RentalResponseDto> getMyRentals(String renterEmail);
    List<RentalResponseDto> getRentalsAsOwner(String ownerEmail);
}
