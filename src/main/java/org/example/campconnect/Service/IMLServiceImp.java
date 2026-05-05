package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
@RequiredArgsConstructor
public class IMLServiceImp implements IMLService {

    // ⭐ URL FastAPI — configurable dans application.properties
    @Value("${ml.api.url:http://localhost:8001}")
    private String mlApiUrl;

    private final RestTemplate restTemplate;

    @Override
    public DropoutPredictionDTO predictDropout(DropoutFeaturesDTO features) {
        String url = mlApiUrl + "/predict-dropout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DropoutFeaturesDTO> request = new HttpEntity<>(features, headers);

        ResponseEntity<DropoutPredictionDTO> response =
                restTemplate.postForEntity(url, request, DropoutPredictionDTO.class);

        return response.getBody();
    }

    @Override
    public EventPredictionDTO predictParticipation(EventFeaturesDTO features) {
        String url = mlApiUrl + "/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EventFeaturesDTO> request = new HttpEntity<>(features, headers);

        ResponseEntity<EventPredictionDTO> response =
                restTemplate.postForEntity(url, request, EventPredictionDTO.class);

        return response.getBody();
    }
}