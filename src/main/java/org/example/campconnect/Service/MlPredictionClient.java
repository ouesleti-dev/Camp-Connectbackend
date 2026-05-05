package org.example.campconnect.Service;

import org.example.campconnect.dto.CandidateFeaturesDTO;
import org.example.campconnect.dto.PredictionResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MlPredictionClient {

    private final WebClient webClient;

    public MlPredictionClient(@Value("${ml.service.url}") String mlServiceUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(mlServiceUrl).build();
    }

    public PredictionResponseDTO predict(CandidateFeaturesDTO features) {
        return this.webClient.post()
                .uri("/predict")
                .bodyValue(features)
                .retrieve()
                .bodyToMono(PredictionResponseDTO.class)
                .block();
    }

    public String checkHealth() {
        return this.webClient.get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
