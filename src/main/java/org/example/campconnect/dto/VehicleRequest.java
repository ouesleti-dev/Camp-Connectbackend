package org.example.campconnect.dto;

import org.example.campconnect.Entity.Role;

public record VehicleRequest(
        String licensePlate,
        String vehicleType,
        Long capacity,
        String status
) {}