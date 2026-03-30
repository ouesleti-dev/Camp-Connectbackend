package org.example.campconnect.Repository;

import org.example.campconnect.Entity.OptionService;
import org.example.campconnect.Entity.OptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionServiceRepository extends JpaRepository<OptionService, Long> {
    List<OptionService> findByOptionType(OptionType optionType);
    List<OptionService> findByVehicleVehicleId(Long vehicleId);
}
