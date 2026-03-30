package org.example.campconnect.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IResponseService;
import org.example.campconnect.dto.ResponseCreateRequest;
import org.example.campconnect.dto.ResponseDTO;
import org.example.campconnect.dto.ResponseUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final IResponseService responseService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getResponseById(id));
    }

    // Toutes les réponses d'un commentaire
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<ResponseDTO>> getByComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(responseService.getResponsesByComment(commentId));
    }

    // Répondre à un commentaire → user connecté associé automatiquement
    @PostMapping
    public ResponseEntity<ResponseDTO> add(@Valid @RequestBody ResponseCreateRequest request,
                                           Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseService.addResponse(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody ResponseUpdateRequest request,
                                              Authentication authentication) {
        return ResponseEntity.ok(responseService.updateResponse(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        responseService.deleteResponse(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}