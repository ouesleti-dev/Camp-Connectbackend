package org.example.campconnect.dto;

public record OptionServiceResponse(
        Long optionId,
        String name,
        String optionType,
        Long vehicleId,
        String vehicleLicensePlate,
        String vehicleType
) {}
