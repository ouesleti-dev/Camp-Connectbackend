package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoryScheduler {

    private final StoryService storyService;

    /**
     * Expire automatiquement les stories dont expiresAt < NOW
     * Exécution : chaque jour à minuit
     *
     * Pour tester en développement, utilise :
     *   @Scheduled(fixedRate = 60000)  → toutes les minutes
     */
    @Scheduled(fixedRate = 60000)
    public void expireOldStories() {
        storyService.expireStories();
    }
}