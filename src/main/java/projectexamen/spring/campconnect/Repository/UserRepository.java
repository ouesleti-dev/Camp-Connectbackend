package projectexamen.spring.campconnect.Repository;

import projectexamen.spring.campconnect.Entity.Role;
import projectexamen.spring.campconnect.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByCamping_CampingId(Long campingId);
}
