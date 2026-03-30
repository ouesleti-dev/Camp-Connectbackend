package org.example.campconnect.dto;

public record AuthResponse(
        String token,
        String email,
        String role,
        Long idUser
)
{}
