package org.example.campconnect.Service;

import org.example.campconnect.Entity.User;
import org.example.campconnect.dto.AuthResponse;
import org.example.campconnect.dto.LoginRequest;
import org.example.campconnect.dto.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
}
