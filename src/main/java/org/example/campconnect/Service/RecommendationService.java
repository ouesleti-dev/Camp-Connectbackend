package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.dto.RecommendationRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RestTemplate restTemplate;
    private static final String AI_URL = "http://localhost:5000/recommend";

    public Map<?, ?> recommend(RecommendationRequestDto dto) {
        return restTemplate.postForObject(AI_URL, dto, Map.class);
    }
}
