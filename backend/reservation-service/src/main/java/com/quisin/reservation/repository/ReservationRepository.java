package com.quisin.reservation.repository;

import com.quisin.reservation.model.Reservation;
import com.quisin.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUserId(String userId);
    
    List<Reservation> findByRestaurantId(String restaurantId);
    
    List<Reservation> findByRestaurantIdAndStatus(String restaurantId, ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId " +
           "AND r.reservationTime BETWEEN :startTime AND :endTime")
    List<Reservation> findByRestaurantIdAndTimeRange(
        @Param("restaurantId") String restaurantId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurantId = :restaurantId " +
           "AND r.reservationTime = :time AND r.status NOT IN ('CANCELLED', 'NO_SHOW')")
    int countActiveReservationsForTimeSlot(
        @Param("restaurantId") String restaurantId,
        @Param("time") LocalDateTime time
    );
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' " +
           "AND r.createdAt < :cutoffTime")
    List<Reservation> findExpiredPendingReservations(
        @Param("cutoffTime") LocalDateTime cutoffTime
    );
    
    boolean existsByRestaurantIdAndTableIdAndReservationTimeAndStatusNot(
        String restaurantId,
        String tableId,
        LocalDateTime reservationTime,
        ReservationStatus status
    );
    
    @Query("SELECT DISTINCT r.tableId FROM Reservation r WHERE r.restaurantId = :restaurantId " +
           "AND r.reservationTime = :time AND r.status NOT IN ('CANCELLED', 'NO_SHOW')")
    List<String> findOccupiedTableIds(
        @Param("restaurantId") String restaurantId,
        @Param("time") LocalDateTime time
    );
} 