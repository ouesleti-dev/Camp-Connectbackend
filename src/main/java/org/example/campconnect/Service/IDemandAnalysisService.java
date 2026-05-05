package org.example.campconnect.Service;

import org.example.campconnect.dto.DemandAnalysisResponse;

public interface IDemandAnalysisService {
    DemandAnalysisResponse analyzeGlobalDemand();

    DemandAnalysisResponse analyzeDemandForDestination(String destination);

    DemandAnalysisResponse analyzeDemandForVehicle(Long vehicleId);
}
