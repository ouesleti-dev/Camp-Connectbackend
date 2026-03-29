package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.Review;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.ReviewRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.ReviewRequestDto;
import org.example.campconnect.dto.ReviewResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IReviewServiceImp implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    private ReviewResponseDto toDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setIdreview(review.getIdreview());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserEmail(review.getUser().getEmail());
        dto.setEquipmentId(review.getEquipment().getIdEquipement());
        return dto;
    }

    @Override
    public ReviewResponseDto addReview(Long equipmentId, ReviewRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        // ✅ Vérifie qu'un seul review par user/equipment
        reviewRepository.findByUser_EmailAndEquipment_IdEquipement(email, equipmentId)
                .ifPresent(r -> { throw new RuntimeException("You already reviewed this equipment"); });

        Review review = Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .user(user)
                .equipment(equipment)
                .build();

        return toDto(reviewRepository.save(review));
    }

    @Override
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Not authorized");
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return toDto(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Not authorized");
        }

        reviewRepository.delete(review);
    }

    @Override
    public void deleteReviewAsAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByEquipment(Long equipmentId) {
        return reviewRepository.findByEquipment_IdEquipement(equipmentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}