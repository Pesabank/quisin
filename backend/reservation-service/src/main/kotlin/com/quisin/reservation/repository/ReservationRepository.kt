package com.quisin.reservation.repository

import com.quisin.reservation.domain.Reservation
import com.quisin.reservation.domain.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ReservationRepository : JpaRepository<Reservation, UUID> {
    // Find reservations by restaurant
    fun findByRestaurantId(restaurantId: UUID): List<Reservation>

    // Find reservations by customer
    fun findByCustomerId(customerId: UUID): List<Reservation>

    // Find reservations by table
    fun findByTableId(tableId: UUID): List<Reservation>

    // Find reservations by status
    fun findByRestaurantIdAndStatus(restaurantId: UUID, status: ReservationStatus): List<Reservation>

    // Find reservations within a date range
    fun findByRestaurantIdAndReservationDateTimeBetween(
        restaurantId: UUID, 
        startDateTime: LocalDateTime, 
        endDateTime: LocalDateTime
    ): List<Reservation>

    // Find active (confirmed or pending) reservations for a specific table
    @Query("""
        SELECT r FROM Reservation r 
        WHERE r.tableId = :tableId 
        AND r.status IN ('CONFIRMED', 'PENDING') 
        AND r.reservationDateTime BETWEEN :startTime AND :endTime
    """)
    fun findActiveReservationsForTable(
        tableId: UUID, 
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): List<Reservation>

    // Count reservations by restaurant and status
    fun countByRestaurantIdAndStatus(restaurantId: UUID, status: ReservationStatus): Long
}
