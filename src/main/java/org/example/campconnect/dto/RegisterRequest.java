package org.example.campconnect.dto;

import org.example.campconnect.Entity.Role;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String phone,
        Role role
){}



