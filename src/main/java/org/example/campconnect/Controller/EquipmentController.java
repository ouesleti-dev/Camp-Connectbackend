package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Service.IEquipmentService;
import org.example.campconnect.dto.EquipmentRequestDto;
import org.example.campconnect.dto.EquipmentResponseDto;
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

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.getOwner().equals(email) && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
