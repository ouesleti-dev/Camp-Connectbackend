package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.dto.MaintenancePredictionDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictiveMaintenanceService {

    private final EquipmentRepository equipmentRepository;
    private final RentalScoreCalculator rentalScoreCalculator;
    private final ReviewScoreCalculator reviewScoreCalculator;
    private final RiskLevelEvaluator riskLevelEvaluator;

    // ======================================================
    // 🔥 1. OLD METHOD (single equipment)
    // ======================================================
    public MaintenancePredictionDTO predict(Long equipmentId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Equipment equipment = equipmentRepository
                .findByOwner(username)
                .stream()
                .filter(e -> e.getIdEquipement().equals(equipmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not allowed or not found"));

        return buildPrediction(equipment);
    }

    // ======================================================
    // 🔥 2. NEW METHOD (USER-BASED → /predict)
    // ======================================================
    public List<MaintenancePredictionDTO> predictForCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        List<Equipment> equipments = equipmentRepository.findByOwner(username);

        return equipments.stream()
                .map(this::buildPrediction)
                .toList();
    }

    // ======================================================
    // 🔥 3. CENTRAL LOGIC (CLEAN CODE)
    // ======================================================
    private MaintenancePredictionDTO buildPrediction(Equipment equipment) {

        Long equipmentId = equipment.getIdEquipement();

        double rentalScore = rentalScoreCalculator.calculate(equipmentId);
        double reviewScore = reviewScoreCalculator.calculate(equipmentId);
        double totalScore  = Math.min(rentalScore + reviewScore, 100);

        return MaintenancePredictionDTO.builder()
                .equipmentId(equipmentId)
                .equipmentName(equipment.getName())
                .totalScore(totalScore)
                .riskLevel(riskLevelEvaluator.getRiskLevel(totalScore))
                .recommendation(riskLevelEvaluator.getRecommendation(totalScore))
                .build();
    }
}