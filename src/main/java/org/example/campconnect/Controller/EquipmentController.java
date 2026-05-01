package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Service.IEquipmentService;
import org.example.campconnect.dto.EquipmentRequestDto;
import org.example.campconnect.dto.EquipmentResponseDto;
import org.example.campconnect.dto.EquipmentStatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final IEquipmentService equipmentService;
    private final EquipmentRepository equipmentRepository;
    @PostMapping
    public ResponseEntity<EquipmentResponseDto> create(
            @RequestBody EquipmentRequestDto dto,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(equipmentService.createEquipment(dto, email));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EquipmentResponseDto>> getMyEquipments(
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(equipmentService.getMyEquipments(email));
    }

    @GetMapping("/verified")
    public ResponseEntity<List<EquipmentResponseDto>> getVerified() {
        return ResponseEntity.ok(equipmentService.getVerifiedEquipments());
    }

    @GetMapping("/unverified")
    public ResponseEntity<List<EquipmentResponseDto>> getUnverified() {
        return ResponseEntity.ok(equipmentService.getUnverifiedEquipments());
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<EquipmentResponseDto> verify(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.verifyEquipment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si admin → supprime directement sans vérification
        if (isAdmin) {
            equipmentService.deleteEquipment(id);
            return ResponseEntity.noContent().build();
        }

        // Si owner → vérifie que c'est bien son équipement
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.getOwner().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        // Vérifie pas de location active avant de supprimer
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponseDto> update(
            @PathVariable Long id,
            @RequestBody EquipmentRequestDto dto,
            Authentication authentication) {

        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String ownerEmail = isAdmin ? null : email;
        return ResponseEntity.ok(equipmentService.updateEquipment(id, dto, ownerEmail));
    }
    @GetMapping("/stats")
    public ResponseEntity<List<EquipmentStatsDto>> getStats() {
        return ResponseEntity.ok(equipmentService.getEquipmentStats());
    }
    @GetMapping("/search")
    public ResponseEntity<List<EquipmentResponseDto>> search(
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) State state,
            @RequestParam(required = false) Float maxPrice) {
        return ResponseEntity.ok(equipmentService.searchEquipments(type, state, maxPrice));
    }
}
