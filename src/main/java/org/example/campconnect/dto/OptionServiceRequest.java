package org.example.campconnect.dto;

public record OptionServiceRequest(
        String name,
        String optionType,
        Long vehicleId
) {}
