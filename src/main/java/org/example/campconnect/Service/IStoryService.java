package org.example.campconnect.Service;

import org.example.campconnect.dto.StoryRequestDto;
import org.example.campconnect.dto.StoryResponseDto;

import java.util.List;

public interface IStoryService {
    StoryResponseDto publishStory(StoryRequestDto dto, String ownerEmail);
    StoryResponseDto updateStory(Long storyId, StoryRequestDto dto, String ownerEmail);
    void deleteStory(Long storyId, String ownerEmail);
    StoryResponseDto applyPromoCode(Long equipmentId, String promoCode);
    List<StoryResponseDto> getActiveStories();
    List<StoryResponseDto> getMyStories(String ownerEmail);
}