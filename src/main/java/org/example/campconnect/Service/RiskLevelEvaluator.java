package org.example.campconnect.Service;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RiskLevelEvaluator {

    public String getRiskLevel(double totalScore) {
        if (totalScore >= 70) return "HIGH";
        if (totalScore >= 40) return "MEDIUM";
        return "LOW";
    }

    public String getRecommendation(double totalScore) {
        if (totalScore >= 70) return "Immediate maintenance required. Stop new rentals.";
        if (totalScore >= 40) return "Schedule maintenance soon. Monitor equipment closely.";
        return "Equipment is in good condition. Continue normal operations.";
    }

    public List<String> getTips(double rentalScore, double reviewScore) {
        List<String> tips = new ArrayList<>();
        if (rentalScore > 30) tips.add("High rental frequency detected — inspect mechanical parts.");
        if (reviewScore > 30) tips.add("Multiple low ratings received — check user-reported issues.");
        if (rentalScore > 20) tips.add("Consider scheduling a preventive maintenance session.");
        if (reviewScore > 20) tips.add("Review customer comments for recurring complaints.");
        if (tips.isEmpty())   tips.add("No immediate action required.");
        return tips;
    }
}