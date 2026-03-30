package org.example.campconnect.Service;

import org.example.campconnect.dto.TransportAdRequest;
import org.example.campconnect.dto.TransportAdResponse;

import java.util.List;

public interface ITransportAdService {
    TransportAdResponse createTransportAd(TransportAdRequest req);
    TransportAdResponse updateTransportAd(Long id, TransportAdRequest req);
    void deleteTransportAd(Long id);
    TransportAdResponse getTransportAdById(Long id);
    List<TransportAdResponse> getAllTransportAds();
    List<TransportAdResponse> getByTransportType(String type);
    List<TransportAdResponse> getByAvailableSeats(Long minSeats);
    List<TransportAdResponse> getByMaxPrice(float maxPrice);
    List<TransportAdResponse> getByTripId(Long tripId);
}
