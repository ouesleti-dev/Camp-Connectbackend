package org.example.campconnect.dto;

public record OptionResponse(
        Long optionId,
        String name,
        Float price,
        String optionType
) {}