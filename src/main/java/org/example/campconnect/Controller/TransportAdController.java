package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ITransportAdService;
import org.example.campconnect.dto.MyTransportAdDetailsResponse;
import org.example.campconnect.dto.TransportAdRequest;
import org.example.campconnect.dto.TransportAdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/transport-ads")
@RequiredArgsConstructor
public class TransportAdController {

    private final ITransportAdService transportAdService;

    @PostMapping
    public ResponseEntity<TransportAdResponse> createTransportAd(@RequestBody TransportAdRequest req) {
        return new ResponseEntity<>(transportAdService.createTransportAd(req), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransportAdResponse>> getAllTransportAds() {
        return ResponseEntity.ok(transportAdService.getAllTransportAds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportAdResponse> getTransportAdById(@PathVariable Long id) {
        return ResponseEntity.ok(transportAdService.getTransportAdById(id));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<TransportAdResponse>> getByTripId(@PathVariable Long tripId) {
        return ResponseEntity.ok(transportAdService.getByTripId(tripId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransportAdResponse>> getByTransportType(@PathVariable String type) {
        return ResponseEntity.ok(transportAdService.getByTransportType(type));
    }

    @GetMapping("/seats/{minSeats}")
    public ResponseEntity<List<TransportAdResponse>> getByAvailableSeats(@PathVariable Long minSeats) {
        return ResponseEntity.ok(transportAdService.getByAvailableSeats(minSeats));
    }

    @GetMapping("/price/{maxPrice}")
    public ResponseEntity<List<TransportAdResponse>> getByMaxPrice(@PathVariable float maxPrice) {
        return ResponseEntity.ok(transportAdService.getByMaxPrice(maxPrice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportAdResponse> updateTransportAd(
            @PathVariable Long id,
            @RequestBody TransportAdRequest req) {
        return ResponseEntity.ok(transportAdService.updateTransportAd(id, req));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteTransportAd(@PathVariable Long id) {
        transportAdService.deleteTransportAd(id);
        return ResponseEntity.ok("TransportAd supprime avec succes");
    }

    @GetMapping("/my-ads-details")
    public ResponseEntity<List<MyTransportAdDetailsResponse>> getMyAdsDetails(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transportAdService.getMyAdsDetails(userDetails.getUsername()));
    }
}
