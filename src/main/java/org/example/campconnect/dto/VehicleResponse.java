package org.example.campconnect.dto;

public record VehicleResponse(
        Long vehicleId,
        String licensePlate,
        String vehicleType,
        Long capacity,
        String status,
        Long ownerId,
        String ownerEmail
) {}