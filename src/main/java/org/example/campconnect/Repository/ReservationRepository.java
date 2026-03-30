package org.example.campconnect.Repository;

import org.example.campconnect.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(String status);
    @Query("select r from User u join u.reservations r where u.email = :email")
    List<Reservation> findByUserEmail(@Param("email") String email);
    @Query("select r from Reservation r where r.transportAd.adId = :transportAdId")
    List<Reservation> findByTransportAdAdId(@Param("transportAdId") Long transportAdId);
    @Query("select u.email from User u join u.reservations r where r.reservationId = :reservationId")
    String findUserEmailByReservationId(@Param("reservationId") Long reservationId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_reservations WHERE reservations_reservation_id = :reservationId", nativeQuery = true)
    void deleteUserReservationLink(@Param("reservationId") Long reservationId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reservation WHERE reservation_id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE transport_ad SET available_seats = available_seats + :seats WHERE ad_id = :adId", nativeQuery = true)
    void incrementSeats(@Param("adId") Long adId, @Param("seats") Long seats);
    @Query(value = "SELECT transport_ad_ad_id FROM reservation WHERE reservation_id = :id", nativeQuery = true)
    Long findTransportAdIdByReservationId(@Param("id") Long id);
    @Query(value = "SELECT seat_count FROM reservation WHERE reservation_id = :id", nativeQuery = true)
    Long findSeatCountByReservationId(@Param("id") Long id);
}
