package org.example.campconnect.Service;

import org.example.campconnect.dto.VehicleRequest;
import org.example.campconnect.dto.VehicleResponse;

import java.util.List;

public interface IVehicleService {
    VehicleResponse addVehicle(VehicleRequest req, String userEmail);
    VehicleResponse updateVehicle(Long id, VehicleRequest req, String userEmail);
    void deleteVehicle(Long id, String userEmail);
    VehicleResponse getVehicleById(Long id);
    List<VehicleResponse> getAllVehicles();
    List<VehicleResponse> getVehiclesByStatus(String status);
    List<VehicleResponse> getVehiclesByType(String vehicleType);
}
