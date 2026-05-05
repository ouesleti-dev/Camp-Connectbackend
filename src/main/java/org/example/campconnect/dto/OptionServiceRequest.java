package org.example.campconnect.dto;

public record OptionServiceRequest(
        String name,
        Float price,
        String optionType,
        Long vehicleId
) {}
