package projectexamen.spring.campconnect.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.Contrat;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {}
