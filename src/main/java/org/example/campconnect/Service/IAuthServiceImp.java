package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.AuthResponse;
import org.example.campconnect.dto.LoginRequest;
import org.example.campconnect.dto.RegisterRequest;
import org.example.campconnect.security.CustomUserDetailsService;
import org.example.campconnect.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IAuthServiceImp implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public User register(RegisterRequest req) {
        if (req.email() == null || req.email().isBlank())
            throw new IllegalArgumentException("Email requis");
        if (userRepository.findByEmail(req.email()).isPresent())
            throw new IllegalArgumentException("Email déjà utilisé");
        if (req.password() == null || req.password().length() < 6)
            throw new IllegalArgumentException("Mot de passe minimum 6 caractères");
        if (req.role() == null)
            throw new IllegalArgumentException("Rôle requis");

        User u = User.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(req.role())
                .enabled(true)
                .build();

        return userRepository.save(u);
    }

    @Override

    public AuthResponse login(LoginRequest req) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun rôle trouvé"))
                .getAuthority();

        // ✅ Récupère l'user depuis la DB pour avoir idUser
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return new AuthResponse(token, userDetails.getUsername(), role, user.getIdUser());
    }
}
