package org.example.campconnect.Repository;

import org.example.campconnect.Entity.TransportAd;
import org.example.campconnect.Entity.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportAdRepository extends JpaRepository<TransportAd, Long> {
    List<TransportAd> findByTransportType(TransportType transportType);
    List<TransportAd> findByAvailableSeatsGreaterThanEqual(Long seats);
    List<TransportAd> findByPriceLessThanEqual(float maxPrice);
    List<TransportAd> findByTripTripId(Long tripId);
}
