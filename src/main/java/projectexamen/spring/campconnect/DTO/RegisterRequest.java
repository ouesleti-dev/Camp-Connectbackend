package projectexamen.spring.campconnect.DTO;

import  projectexamen.spring.campconnect.Entity.Role;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String phone,
        Role role
)
{}
