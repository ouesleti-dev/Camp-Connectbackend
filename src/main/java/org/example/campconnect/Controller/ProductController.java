package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IProductService;
import org.example.campconnect.dto.ProductRequestDTO;
import org.example.campconnect.dto.ProductResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor


public class ProductController {
    private final IProductService productService;
    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.addProduct(dto));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ProductResponseDTO>> getApproved() {
        return ResponseEntity.ok(productService.getApprovedProducts());
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProductResponseDTO>> getMyProducts() {
        return ResponseEntity.ok(productService.getMyProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ── Admin only ──────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProductResponseDTO>> getPending() {
        return ResponseEntity.ok(productService.getPendingProducts());
    }

    // approve → PUT
    @PutMapping("/{id}/approve")
    public ResponseEntity<ProductResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(productService.approveProduct(id));
    }

    // reject → PUT
    @PutMapping("/{id}/reject")
    public ResponseEntity<ProductResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(productService.rejectProduct(id));
    }

}
