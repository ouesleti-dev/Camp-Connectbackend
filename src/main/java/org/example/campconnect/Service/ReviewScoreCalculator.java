package org.example.campconnect.Service;

import org.example.campconnect.Repository.ReviewRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewScoreCalculator {

    private final ReviewRepository reviewRepository;

    public double calculate(Long equipmentId) {
        Double avgRating  = reviewRepository.findAverageRatingByEquipmentId(equipmentId);
        long lowRatings   = reviewRepository.countLowRatings(equipmentId, 2); // threshold ≤ 2
        long totalReviews = reviewRepository.findByEquipment_IdEquipement(equipmentId).size();

        if (totalReviews == 0) return 0;

        double score = 0;

        // Rule 1: average rating (inverted — low rating = high risk)
        if (avgRating != null) {
            if (avgRating < 2.0)      score += 35;
            else if (avgRating < 3.0) score += 25;
            else if (avgRating < 3.5) score += 15;
            else if (avgRating < 4.0) score += 8;
            // 4+ → no score added
        }

        // Rule 2: proportion of bad reviews
        double badRatio = (double) lowRatings / totalReviews;
        if (badRatio > 0.5)      score += 15;
        else if (badRatio > 0.3) score += 10;
        else if (badRatio > 0.1) score += 5;

        return Math.min(score, 50); // cap at 50
    }
}