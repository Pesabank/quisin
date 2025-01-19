package com.quisin.reservation.repository

import com.quisin.reservation.domain.RestaurantTable
import com.quisin.reservation.domain.TableStatus
import com.quisin.reservation.domain.TableType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface TableRepository : JpaRepository<RestaurantTable, UUID> {
    // Find tables by restaurant
    fun findByRestaurantId(restaurantId: UUID): List<RestaurantTable>

    // Find tables by status
    fun findByRestaurantIdAndStatus(restaurantId: UUID, status: TableStatus): List<RestaurantTable>

    // Find tables by type
    fun findByRestaurantIdAndType(restaurantId: UUID, type: TableType): List<RestaurantTable>

    // Find tables by capacity
    fun findByRestaurantIdAndCapacityGreaterThanEqual(restaurantId: UUID, capacity: Int): List<RestaurantTable>

    // Find available tables for a specific time and party size
    @Query("""
        SELECT t FROM RestaurantTable t 
        WHERE t.restaurantId = :restaurantId 
        AND t.status = 'AVAILABLE' 
        AND t.capacity >= :partySize 
        AND t.id NOT IN (
            SELECT r.tableId 
            FROM Reservation r 
            WHERE r.reservationDateTime BETWEEN :startTime AND :endTime 
            AND r.status IN ('CONFIRMED', 'PENDING')
        )
    """)
    fun findAvailableTables(
        restaurantId: UUID, 
        partySize: Int, 
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): List<RestaurantTable>

    // Count tables by restaurant and status
    fun countByRestaurantIdAndStatus(restaurantId: UUID, status: TableStatus): Long
}
