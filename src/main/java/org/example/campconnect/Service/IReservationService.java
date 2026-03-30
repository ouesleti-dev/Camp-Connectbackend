package org.example.campconnect.Service;

import org.example.campconnect.dto.ReservationRequest;
import org.example.campconnect.dto.ReservationResponse;

import java.util.List;

public interface IReservationService {
    ReservationResponse createReservation(ReservationRequest req, String userEmail);
    ReservationResponse updateReservation(Long id, ReservationRequest req, String userEmail);
    void deleteReservation(Long id);
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getAllReservations();
    List<ReservationResponse> getByStatus(String status);
    List<ReservationResponse> getByUserEmail(String email);
    List<ReservationResponse> getByTransportAdId(Long transportAdId);
}
