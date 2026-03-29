package projectexamen.spring.campconnect.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectexamen.spring.campconnect.Entity.offer;

@Repository
public interface OfferRepository extends JpaRepository<offer, Long> {}