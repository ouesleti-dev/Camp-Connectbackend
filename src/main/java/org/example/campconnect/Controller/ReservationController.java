package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IReservationService;
import org.example.campconnect.dto.ReservationDetailsResponse;
import org.example.campconnect.dto.ReservationRequest;
import org.example.campconnect.dto.ReservationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                reservationService.createReservation(req, userDetails.getUsername())
        );
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/details")
    public ResponseEntity<List<ReservationDetailsResponse>> getDetailedReservations() {
        return ResponseEntity.ok(reservationService.getDetailedReservations());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationDetailsResponse>> searchByDestinationAndTransportType(
            @RequestParam String destination,
            @RequestParam String transportType) {
        return ResponseEntity.ok(
                reservationService.searchByDestinationAndTransportType(destination, transportType)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getByUserEmail(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationService.getByUserEmail(userDetails.getUsername()));
    }

    @GetMapping("/ad/{adId}")
    public ResponseEntity<List<ReservationResponse>> getByTransportAdId(@PathVariable Long adId) {
        return ResponseEntity.ok(reservationService.getByTransportAdId(adId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reservationService.getByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationService.updateReservation(id, req, userDetails.getUsername()));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("Reservation supprimee avec succes");
    }
}
