package projectexamen.spring.campconnect.Services;

import projectexamen.spring.campconnect.Entity.User;
import projectexamen.spring.campconnect.DTO.AuthResponse;
import projectexamen.spring.campconnect.DTO.LoginRequest;
import projectexamen.spring.campconnect.DTO.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
}
