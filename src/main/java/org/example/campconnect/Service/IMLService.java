package org.example.campconnect.Service;

import org.example.campconnect.dto.DropoutFeaturesDTO;
import org.example.campconnect.dto.DropoutPredictionDTO;
import org.example.campconnect.dto.EventPredictionDTO;
import org.example.campconnect.dto.EventFeaturesDTO;

public interface IMLService {
    DropoutPredictionDTO predictDropout(DropoutFeaturesDTO features);
    EventPredictionDTO   predictParticipation(EventFeaturesDTO features);
}