package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Entity.Vehicle;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.Repository.VehicleRepository;
import org.example.campconnect.dto.VehicleRequest;
import org.example.campconnect.dto.VehicleResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements IVehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    private VehicleResponse toResponse(Vehicle vehicle, User owner) {
        return new VehicleResponse(
                vehicle.getVehicleId(),
                vehicle.getLicensePlate(),
                vehicle.getVehicleType(),
                vehicle.getCapacity(),
                vehicle.getStatus(),
                owner != null ? owner.getIdUser() : null,
                owner != null ? owner.getEmail() : null
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest req, String userEmail) {
        if (req.licensePlate() == null || req.licensePlate().isBlank()) {
            throw new IllegalArgumentException("La plaque d'immatriculation est requise");
        }
        if (vehicleRepository.findByLicensePlate(req.licensePlate()).isPresent()) {
            throw new IllegalArgumentException("Cette plaque existe deja");
        }
        if (req.vehicleType() == null || req.vehicleType().isBlank()) {
            throw new IllegalArgumentException("Le type de vehicule est requis");
        }
        if (req.capacity() == null || req.capacity() <= 0) {
            throw new IllegalArgumentException("La capacite doit etre positive");
        }
        if (req.status() == null || req.status().isBlank()) {
            throw new IllegalArgumentException("Le statut est requis");
        }

        User owner = getUserByEmail(userEmail);

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(req.licensePlate());
        vehicle.setVehicleType(req.vehicleType());
        vehicle.setCapacity(req.capacity());
        vehicle.setStatus(req.status());

        Vehicle saved = vehicleRepository.save(vehicle);

        if (owner.getVehicles() == null) {
            owner.setVehicles(new ArrayList<>());
        }
        owner.getVehicles().add(saved);
        userRepository.saveAndFlush(owner);

        return toResponse(saved, owner);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRequest req, String userEmail) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable avec l'id : " + id));

        vehicle.setLicensePlate(req.licensePlate());
        vehicle.setVehicleType(req.vehicleType());
        vehicle.setCapacity(req.capacity());
        vehicle.setStatus(req.status());

        User owner = getUserByEmail(userEmail);
        return toResponse(vehicleRepository.save(vehicle), owner);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id, String userEmail) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable avec l'id : " + id));

        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getVehicles() != null) {
                user.getVehicles().removeIf(v -> v.getVehicleId().equals(id));
                userRepository.save(user);
            }
        }

        vehicleRepository.deleteById(vehicle.getVehicleId());
    }

    @Override
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable avec l'id : " + id));
        return toResponse(vehicle, null);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(v -> toResponse(v, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> getVehiclesByStatus(String status) {
        return vehicleRepository.findByStatus(status)
                .stream()
                .map(v -> toResponse(v, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> getVehiclesByType(String vehicleType) {
        return vehicleRepository.findByVehicleType(vehicleType)
                .stream()
                .map(v -> toResponse(v, null))
                .collect(Collectors.toList());
    }
}
