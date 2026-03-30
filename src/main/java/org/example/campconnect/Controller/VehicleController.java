package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IVehicleService;
import org.example.campconnect.dto.VehicleRequest;
import org.example.campconnect.dto.VehicleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final IVehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(
            @RequestBody VehicleRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        VehicleResponse response = vehicleService.addVehicle(req, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VehicleResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<VehicleResponse>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(vehicleService.getVehiclesByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        VehicleResponse response = vehicleService.updateVehicle(id, req, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        vehicleService.deleteVehicle(id, userDetails.getUsername());
        return ResponseEntity.ok("Vehicule supprime avec succes");
    }
}
