package org.example.campconnect.Service;

import org.example.campconnect.dto.TripRequest;
import org.example.campconnect.dto.TripResponse;

import java.util.Date;
import java.util.List;

public interface ITripService {
    TripResponse createTrip(TripRequest req);
    TripResponse updateTrip(Long id, TripRequest req);
    void deleteTrip(Long id);
    TripResponse getTripById(Long id);
    List<TripResponse> getAllTrips();
    List<TripResponse> getTripsByVehicleId(Long vehicleId);
    List<TripResponse> getTripsByDestination(String destination);
    List<TripResponse> getTripsByDeparture(String departureLocation);
    List<TripResponse> getUpcomingTrips(Date fromDate);
}
