package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Entity.Equipment;
import org.example.campconnect.Entity.Story;
import org.example.campconnect.Entity.User;
import org.example.campconnect.Repository.EquipmentRepository;
import org.example.campconnect.Repository.StoryRepository;
import org.example.campconnect.Repository.UserRepository;
import org.example.campconnect.dto.StoryRequestDto;
import org.example.campconnect.dto.StoryResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService implements IStoryService {

    private final StoryRepository storyRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────
    // PUBLIER UNE STORY
    // ─────────────────────────────────────────
    @Override
    @Transactional
    public StoryResponseDto publishStory(StoryRequestDto dto, String ownerEmail) {

        // 1. Récupérer l'équipement
        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        // 2. Vérifier que l'appelant est le propriétaire
        if (!equipment.getOwner().equalsIgnoreCase(ownerEmail)) {
            throw new RuntimeException("Not authorized: you are not the owner of this equipment");
        }

        // 3a. ✅ Auto-expirer les stories périmées de cet équipement avant vérification
        List<Story> toExpire = storyRepository
                .findExpiredActiveStoriesByEquipment(dto.getEquipmentId(), new Date());
        toExpire.forEach(s -> s.setActive(false));
        storyRepository.saveAll(toExpire);

        // 3b. ✅ Vérifier qu'il n'y a pas déjà une story active ET non expirée
        if (storyRepository.existsActiveAndNotExpiredStoryForEquipment(
                dto.getEquipmentId(), new Date())) {
            throw new RuntimeException(
                    "This equipment already has an active story. Delete or update it first.");
        }

        // 4. Valider le discount
        validateDiscount(dto.getDiscount());

        // 5. Récupérer l'utilisateur propriétaire
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 6. Créer la story (durée : 24h)
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + TimeUnit.HOURS.toMillis(24));

        Story story = Story.builder()
                .promoCode(dto.getPromoCode().toUpperCase().trim())
                .discount(dto.getDiscount())
                .message(dto.getMessage())
                .active(true)
                .createdAt(now)
                .expiresAt(expiresAt)
                .equipment(equipment)
                .user(owner)
                .build();

        Story saved = storyRepository.save(story);
        return toDto(saved);
    }

    // ─────────────────────────────────────────
    // MODIFIER UNE STORY EXISTANTE
    // ─────────────────────────────────────────
    @Override
    @Transactional
    public StoryResponseDto updateStory(Long storyId, StoryRequestDto dto, String ownerEmail) {

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        // Vérifier propriété
        if (!story.getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new RuntimeException("Not authorized");
        }

        // Vérifier que la story est encore active et non expirée
        if (!Boolean.TRUE.equals(story.getActive())) {
            throw new RuntimeException("Cannot update an expired story");
        }

        // ✅ Vérifier aussi la date d'expiration
        if (story.getExpiresAt() != null && story.getExpiresAt().before(new Date())) {
            story.setActive(false);
            storyRepository.save(story);
            throw new RuntimeException("Cannot update an expired story");
        }

        validateDiscount(dto.getDiscount());

        // Mise à jour — on conserve l'expiresAt d'origine
        story.setPromoCode(dto.getPromoCode().toUpperCase().trim());
        story.setDiscount(dto.getDiscount());
        story.setMessage(dto.getMessage());

        return toDto(storyRepository.save(story));
    }

    // ─────────────────────────────────────────
    // SUPPRIMER UNE STORY
    // ─────────────────────────────────────────
    @Override
    @Transactional
    public void deleteStory(Long storyId, String ownerEmail) {

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        if (!story.getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new RuntimeException("Not authorized");
        }

        storyRepository.deleteById(storyId);
    }

    // ─────────────────────────────────────────
    // APPLIQUER UN CODE PROMO
    // ─────────────────────────────────────────
    @Override
    public StoryResponseDto applyPromoCode(Long equipmentId, String promoCode) {

        Story story = storyRepository
                .findByEquipment_IdEquipementAndActiveTrue(equipmentId)
                .orElseThrow(() -> new RuntimeException("No active story on this equipment"));

        // ✅ Vérifier que la story n'est pas expirée
        if (story.getExpiresAt() != null && story.getExpiresAt().before(new Date())) {
            story.setActive(false);
            storyRepository.save(story);
            throw new RuntimeException("Story has expired");
        }

        // Vérifier le code promo (insensible à la casse)
        if (!story.getPromoCode().equalsIgnoreCase(promoCode.trim())) {
            throw new RuntimeException("Invalid promo code");
        }

        return toDto(story);
    }

    // ─────────────────────────────────────────
    // LISTER TOUTES LES STORIES ACTIVES (public)
    // ─────────────────────────────────────────
    @Override
    public List<StoryResponseDto> getActiveStories() {
        return storyRepository.findByActiveTrue()
                .stream()
                .filter(s -> s.getExpiresAt() != null && s.getExpiresAt().after(new Date()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // MES STORIES (propriétaire connecté)
    // ─────────────────────────────────────────
    @Override
    public List<StoryResponseDto> getMyStories(String ownerEmail) {
        return storyRepository.findByUser_EmailAndActiveTrue(ownerEmail)
                .stream()
                // ✅ Filtrer aussi par date d'expiration
                .filter(s -> s.getExpiresAt() != null && s.getExpiresAt().after(new Date()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // EXPIRER LES STORIES — appelée par le scheduler
    // ─────────────────────────────────────────
    @Transactional
    public void expireStories() {
        List<Story> expired = storyRepository.findExpiredStories(new Date());
        expired.forEach(s -> s.setActive(false));
        storyRepository.saveAll(expired);
    }

    // ─────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────
    private void validateDiscount(Float discount) {
        if (discount == null || discount <= 0 || discount >= 100) {
            throw new RuntimeException("Discount must be between 1 and 99%");
        }
    }

    private StoryResponseDto toDto(Story s) {
        long minutesRemaining = 0;
        if (s.getExpiresAt() != null) {
            long diff = s.getExpiresAt().getTime() - new Date().getTime();
            minutesRemaining = Math.max(0, TimeUnit.MILLISECONDS.toMinutes(diff));
        }

        Float originalPrice = s.getEquipment().getPrice();
        Float discountedPrice = null;
        if (originalPrice != null && s.getDiscount() != null) {
            discountedPrice = Math.round(originalPrice * (1 - s.getDiscount() / 100) * 100) / 100.0f;
        }

        return new StoryResponseDto(
                s.getIdStory(),
                s.getEquipment().getIdEquipement(),
                s.getEquipment().getName(),
                s.getUser().getEmail(),
                originalPrice,
                discountedPrice,
                s.getDiscount(),
                s.getPromoCode(),
                s.getMessage(),
                s.getActive(),
                s.getCreatedAt(),
                s.getExpiresAt(),
                minutesRemaining
        );
    }
}