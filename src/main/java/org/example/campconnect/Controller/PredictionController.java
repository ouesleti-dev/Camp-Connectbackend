package org.example.campconnect.Controller;

import org.example.campconnect.Service.MlPredictionClient;
import org.example.campconnect.dto.CandidateFeaturesDTO;
import org.example.campconnect.dto.PredictionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PredictionController {

    private final MlPredictionClient mlPredictionClient;

    @Autowired
    public PredictionController(MlPredictionClient mlPredictionClient) {
        this.mlPredictionClient = mlPredictionClient;
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictionResponseDTO> predict(@RequestBody CandidateFeaturesDTO features) {
        PredictionResponseDTO response = mlPredictionClient.predict(features);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ml/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(mlPredictionClient.checkHealth());
    }
}
