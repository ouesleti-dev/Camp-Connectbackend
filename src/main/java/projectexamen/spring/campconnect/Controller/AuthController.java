package projectexamen.spring.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import projectexamen.spring.campconnect.Services.IAuthService;
import projectexamen.spring.campconnect.DTO.AuthResponse;
import projectexamen.spring.campconnect.DTO.LoginRequest;
import projectexamen.spring.campconnect.DTO.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
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
