package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Service.IAuthService;
import org.example.campconnect.dto.AuthResponse;
import org.example.campconnect.dto.LoginRequest;
import org.example.campconnect.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        var saved = authService.register(req);
        return ResponseEntity.ok("User créé : " + saved.getEmail());
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
