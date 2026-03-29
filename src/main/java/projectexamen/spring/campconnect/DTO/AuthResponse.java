package projectexamen.spring.campconnect.DTO;

public record AuthResponse(
        String token,
        String email,
        String role
)
{}
