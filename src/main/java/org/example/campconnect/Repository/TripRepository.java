package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByDestination(String destination);
    List<Trip> findByDepartureLocation(String departureLocation);
    List<Trip> findByDepartureDateAfter(Date date);
    List<Trip> findByDepartureLocationAndDestination(String departure, String destination);
    List<Trip> findByVehicleVehicleId(Long vehicleId);
}
