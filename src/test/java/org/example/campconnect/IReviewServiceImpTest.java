package org.example.campconnect;


import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.Review;
import org.example.campconnect.Entity.State;
import org.example.campconnect.Entity.Type;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.ReviewRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.Service.IReviewServiceImp;
import org.example.campconnect.dto.ReviewRequestDto;
import org.example.campconnect.dto.ReviewResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IReviewServiceImpTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private EquipmentRepository equipmentRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private IReviewServiceImp reviewService;

    private User user;
    private Equipment equipment;
    private Review review;
    private ReviewRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@test.com");

        equipment = Equipment.builder()
                .idEquipement(1L)
                .name("Tente")
                .type(Type.TENTS)
                .owner("owner@test.com")
                .verified(true)
                .state(State.Not_Reserve)
                .price(20.0f)
                .build();

        review = Review.builder()
                .idreview(5L)
                .rating(4)
                .comment("Très bien")
                .user(user)
                .equipment(equipment)
                .build();

        requestDto = new ReviewRequestDto();
        requestDto.setRating(4);
        requestDto.setComment("Très bien");
    }

    // ───────────────────────────────────────────────
    // addReview
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("addReview → crée un review avec succès")
    void addReview_success() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(reviewRepository.findByUser_EmailAndEquipment_IdEquipement("user@test.com", 1L))
                .thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponseDto result = reviewService.addReview(1L, requestDto, "user@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Très bien");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("addReview → lève exception si user introuvable")
    void addReview_userNotFound() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(1L, requestDto, "nobody@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("addReview → lève exception si équipement introuvable")
    void addReview_equipmentNotFound() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(99L, requestDto, "user@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipment not found");
    }

    @Test
    @DisplayName("addReview → lève exception si review déjà existant pour cet user/equipment")
    void addReview_alreadyReviewed() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(reviewRepository.findByUser_EmailAndEquipment_IdEquipement("user@test.com", 1L))
                .thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.addReview(1L, requestDto, "user@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You already reviewed this equipment");
    }

    // ───────────────────────────────────────────────
    // updateReview
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("updateReview → met à jour le review")
    void updateReview_success() {
        when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        requestDto.setRating(5);
        requestDto.setComment("Excellent");
        ReviewResponseDto result = reviewService.updateReview(5L, requestDto, "user@test.com");

        assertThat(result).isNotNull();
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("updateReview → lève exception si review introuvable")
    void updateReview_notFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(99L, requestDto, "user@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Review not found");
    }

    @Test
    @DisplayName("updateReview → lève exception si non autorisé")
    void updateReview_notAuthorized() {
        when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(5L, requestDto, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not authorized");
    }

    // ───────────────────────────────────────────────
    // deleteReview
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("deleteReview → user peut supprimer son propre review")
    void deleteReview_success() {
        when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(5L, "user@test.com");

        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("deleteReview → lève exception si review introuvable")
    void deleteReview_notFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(99L, "user@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Review not found");
    }

    @Test
    @DisplayName("deleteReview → lève exception si non autorisé")
    void deleteReview_notAuthorized() {
        when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(5L, "hacker@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not authorized");
    }

    // ───────────────────────────────────────────────
    // deleteReviewAsAdmin
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("deleteReviewAsAdmin → admin peut supprimer n'importe quel review")
    void deleteReviewAsAdmin_success() {
        when(reviewRepository.findById(5L)).thenReturn(Optional.of(review));

        reviewService.deleteReviewAsAdmin(5L);

        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("deleteReviewAsAdmin → lève exception si review introuvable")
    void deleteReviewAsAdmin_notFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReviewAsAdmin(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Review not found");
    }

    // ───────────────────────────────────────────────
    // getReviewsByEquipment
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("getReviewsByEquipment → retourne les reviews d'un équipement")
    void getReviewsByEquipment_success() {
        when(reviewRepository.findByEquipment_IdEquipement(1L)).thenReturn(List.of(review));

        List<ReviewResponseDto> result = reviewService.getReviewsByEquipment(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEquipmentId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getReviewsByEquipment → retourne liste vide si aucun review")
    void getReviewsByEquipment_empty() {
        when(reviewRepository.findByEquipment_IdEquipement(99L)).thenReturn(List.of());

        List<ReviewResponseDto> result = reviewService.getReviewsByEquipment(99L);

        assertThat(result).isEmpty();
    }
}