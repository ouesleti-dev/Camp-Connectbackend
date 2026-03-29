package org.example.campconnect.Service;

import org.example.campconnect.dto.ReviewRequestDto;
import org.example.campconnect.dto.ReviewResponseDto;

import java.util.List;

public interface IReviewService {
    ReviewResponseDto addReview(Long equipmentId, ReviewRequestDto dto, String email);
    ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto, String email);
    void deleteReview(Long reviewId, String email);
    void deleteReviewAsAdmin(Long reviewId);
    List<ReviewResponseDto> getReviewsByEquipment(Long equipmentId);
}