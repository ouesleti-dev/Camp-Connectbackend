package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.ITripService;
import org.example.campconnect.dto.TripRequest;
import org.example.campconnect.dto.TripResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final ITripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@RequestBody TripRequest req) {
        return new ResponseEntity<>(tripService.createTrip(req), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<TripResponse>> getTripsByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(tripService.getTripsByVehicleId(vehicleId));
    }

    @GetMapping("/destination/{dest}")
    public ResponseEntity<List<TripResponse>> getTripsByDestination(@PathVariable String dest) {
        return ResponseEntity.ok(tripService.getTripsByDestination(dest));
    }

    @GetMapping("/departure/{departure}")
    public ResponseEntity<List<TripResponse>> getTripsByDeparture(@PathVariable String departure) {
        return ResponseEntity.ok(tripService.getTripsByDeparture(departure));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TripResponse>> getUpcomingTrips(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fromDate) {
        return ResponseEntity.ok(tripService.getUpcomingTrips(fromDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(@PathVariable Long id, @RequestBody TripRequest req) {
        return ResponseEntity.ok(tripService.updateTrip(id, req));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok("Trip supprime avec succes");
    }
}
