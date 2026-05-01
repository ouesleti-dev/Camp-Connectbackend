package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Reservation;
import org.example.campconnect.Entity.TransportType;
import org.example.campconnect.dto.ReservationDetailsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.example.campconnect.dto.GroupedStatResponse;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(String status);

    @Query("select r from User u join u.reservations r where u.email = :email")
    List<Reservation> findByUserEmail(@Param("email") String email);

    @Query("select r from Reservation r where r.transportAd.adId = :transportAdId")
    List<Reservation> findByTransportAdAdId(@Param("transportAdId") Long transportAdId);

    @Query("""
        SELECT new org.example.campconnect.dto.ReservationDetailsResponse(
            r.reservationId,
            r.reservationDate,
            r.seatCount,
            r.status,
            ad.price,
            ad.transportType,
            t.destination,
            v.licensePlate
        )
        FROM Reservation r
        JOIN r.transportAd ad
        JOIN ad.trip t
        JOIN t.vehicle v
    """)
    List<ReservationDetailsResponse> findDetailedReservations();

    List<Reservation> findByTransportAdTripDestinationIgnoreCaseAndTransportAdTransportType(
            String destination,
            TransportType transportType
    );

    @Query("select u.email from User u join u.reservations r where r.reservationId = :reservationId")
    String findUserEmailByReservationId(@Param("reservationId") Long reservationId);


    @Query("""
    SELECT COALESCE(SUM(r.seatCount), 0)
    FROM Reservation r
""")
    Long sumReservedSeats();

    @Query("""
    SELECT COALESCE(SUM(r.seatCount * ad.price), 0)
    FROM Reservation r
    JOIN r.transportAd ad
""")
    Double calculateTotalRevenue();

    @Query("""
    SELECT new org.example.campconnect.dto.GroupedStatResponse(
        CAST(ad.transportType AS string),
        COUNT(r)
    )
    FROM Reservation r
    JOIN r.transportAd ad
    GROUP BY ad.transportType
""")
    List<GroupedStatResponse> countReservationsByTransportType();

    @Query("""
    SELECT new org.example.campconnect.dto.GroupedStatResponse(
        trip.destination,
        COUNT(r)
    )
    FROM Reservation r
    JOIN r.transportAd ad
    JOIN ad.trip trip
    GROUP BY trip.destination
""")
    List<GroupedStatResponse> countReservationsByDestination();
}
