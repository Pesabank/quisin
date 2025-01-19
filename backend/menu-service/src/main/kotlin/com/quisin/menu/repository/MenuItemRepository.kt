package com.quisin.menu.repository

import com.quisin.menu.domain.MenuItem
import com.quisin.menu.domain.MenuItemCategory
import com.quisin.menu.domain.MenuItemStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, UUID> {
    // Find menu items by restaurant
    fun findByRestaurantId(restaurantId: UUID): List<MenuItem>

    // Find menu items by category
    fun findByRestaurantIdAndCategory(restaurantId: UUID, category: MenuItemCategory): List<MenuItem>

    // Find menu items by status
    fun findByRestaurantIdAndStatus(restaurantId: UUID, status: MenuItemStatus): List<MenuItem>

    // Find menu items with stock below a certain threshold
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.currentStock < m.maxStock * 0.2")
    fun findLowStockItems(restaurantId: UUID): List<MenuItem>

    // Search menu items by name or description
    fun findByRestaurantIdAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        restaurantId: UUID, 
        name: String, 
        description: String
    ): List<MenuItem>

    // Find menu items within a price range
    fun findByRestaurantIdAndPriceBetween(
        restaurantId: UUID, 
        minPrice: BigDecimal, 
        maxPrice: BigDecimal
    ): List<MenuItem>

    // Find menu items with specific tags
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND :tag MEMBER OF m.tags")
    fun findByRestaurantIdAndTag(restaurantId: UUID, tag: String): List<MenuItem>
}
