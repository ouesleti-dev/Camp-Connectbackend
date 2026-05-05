package org.example.campconnect.dto;

public record OptionServiceResponse(
        Long optionId,
        String name,
        Float price,
        String optionType,
        Long vehicleId,
        String vehicleLicensePlate,
        String vehicleType
) {}
