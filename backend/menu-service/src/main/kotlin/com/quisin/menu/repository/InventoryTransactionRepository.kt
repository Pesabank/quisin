package com.quisin.menu.repository

import com.quisin.menu.domain.InventoryTransaction
import com.quisin.menu.domain.InventoryTransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface InventoryTransactionRepository : JpaRepository<InventoryTransaction, UUID> {
    // Find transactions by restaurant
    fun findByRestaurantId(restaurantId: UUID): List<InventoryTransaction>

    // Find transactions by menu item
    fun findByMenuItemId(menuItemId: UUID): List<InventoryTransaction>

    // Find transactions by type
    fun findByRestaurantIdAndTransactionType(
        restaurantId: UUID, 
        transactionType: InventoryTransactionType
    ): List<InventoryTransaction>

    // Find transactions within a date range
    fun findByRestaurantIdAndCreatedAtBetween(
        restaurantId: UUID, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<InventoryTransaction>

    // Find transactions by employee
    fun findByEmployeeId(employeeId: UUID): List<InventoryTransaction>

    // Calculate total stock in/out for a menu item
    @Query("""
        SELECT 
            SUM(CASE WHEN t.transactionType = 'STOCK_IN' THEN t.quantity 
                     WHEN t.transactionType = 'STOCK_OUT' THEN -t.quantity 
                     WHEN t.transactionType = 'SPOILAGE' THEN -t.quantity 
                     ELSE 0 END) 
        FROM InventoryTransaction t 
        WHERE t.menuItemId = :menuItemId
    """)
    fun calculateNetStockChange(menuItemId: UUID): Int
}
