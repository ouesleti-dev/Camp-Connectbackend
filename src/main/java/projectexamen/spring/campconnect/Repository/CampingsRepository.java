package projectexamen.spring.campconnect.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.campings;

@Repository
public interface CampingsRepository extends JpaRepository<campings, Long> {}
