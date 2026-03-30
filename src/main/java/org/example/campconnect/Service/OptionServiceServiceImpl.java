package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.OptionService;
import org.example.campconnect.Entity.OptionType;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.OptionServiceRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.OptionServiceRequest;
import org.example.campconnect.dto.OptionServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionServiceServiceImpl implements IOptionServiceService {

    private final OptionServiceRepository optionServiceRepository;
    private final VehicleRepository vehicleRepository;

    private OptionServiceResponse toResponse(OptionService optionService) {
        Vehicle vehicle = optionService.getVehicle();
        return new OptionServiceResponse(
                optionService.getOptionId(),
                optionService.getName(),
                optionService.getOptionType().name(),
                vehicle != null ? vehicle.getVehicleId() : null,
                vehicle != null ? vehicle.getLicensePlate() : null,
                vehicle != null ? vehicle.getVehicleType() : null
        );
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'option est requis");
        }
    }

    private OptionType parseOptionType(String optionType) {
        if (optionType == null || optionType.isBlank()) {
            throw new IllegalArgumentException("Le type d'option est requis");
        }

        try {
            return OptionType.valueOf(optionType.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Type d'option invalide. Valeurs autorisees: " + Arrays.toString(OptionType.values())
            );
        }
    }

    private Vehicle getVehicleById(Long vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Le vehicleId est requis");
        }

        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable avec l'id : " + vehicleId));
    }

    @Override
    @Transactional
    public OptionServiceResponse createOptionService(OptionServiceRequest req) {
        validateName(req.name());
        OptionType optionType = parseOptionType(req.optionType());
        Vehicle vehicle = getVehicleById(req.vehicleId());

        OptionService optionService = new OptionService();
        optionService.setName(req.name().trim());
        optionService.setOptionType(optionType);
        optionService.setVehicle(vehicle);

        return toResponse(optionServiceRepository.save(optionService));
    }

    @Override
    @Transactional
    public OptionServiceResponse updateOptionService(Long id, OptionServiceRequest req) {
        validateName(req.name());
        OptionType optionType = parseOptionType(req.optionType());
        Vehicle vehicle = getVehicleById(req.vehicleId());

        OptionService existing = optionServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OptionService introuvable avec l'id : " + id));

        existing.setName(req.name().trim());
        existing.setOptionType(optionType);
        existing.setVehicle(vehicle);

        return toResponse(optionServiceRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteOptionService(Long id) {
        OptionService existing = optionServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OptionService introuvable avec l'id : " + id));
        optionServiceRepository.deleteById(existing.getOptionId());
    }

    @Override
    public OptionServiceResponse getOptionServiceById(Long id) {
        return optionServiceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("OptionService introuvable avec l'id : " + id));
    }

    @Override
    public List<OptionServiceResponse> getAllOptionServices() {
        return optionServiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OptionServiceResponse> getByOptionType(String optionType) {
        OptionType parsedType = parseOptionType(optionType);
        return optionServiceRepository.findByOptionType(parsedType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OptionServiceResponse> getByVehicleId(Long vehicleId) {
        return optionServiceRepository.findByVehicleVehicleId(vehicleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
