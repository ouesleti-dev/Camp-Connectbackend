package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Role;
import org.example.campconnect.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);

    List<User> findByCamping_CampingId(Long campingId);
}
