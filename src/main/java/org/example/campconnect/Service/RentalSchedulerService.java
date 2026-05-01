package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campconnect.Entity.Rental;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.RentalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalSchedulerService {

    private final RentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;

    // Exécute tous les jours à minuit
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processExpiredRentals() {
        log.info("=== [SCHEDULER] Vérification des rentals expirés : {}", new Date());

        Date now = new Date();

        // 1. Récupère les rentals acceptés dont la date de fin est passée
        List<Rental> expiredRentals = rentalRepository.findExpiredAcceptedRentals(now);

        log.info("[SCHEDULER] {} rentals expirés trouvés", expiredRentals.size());

        for (Rental rental : expiredRentals) {
            // 2. Remet l'équipement en état "Not_Reserve"
            rental.getEquipment().setState(State.Not_Reserve);
            equipmentRepository.save(rental.getEquipment());

            log.info("[SCHEDULER] Équipement '{}' remis à Not_Reserve",
                    rental.getEquipment().getName());
        }

        log.info("=== [SCHEDULER] Traitement terminé ===");
    }
}