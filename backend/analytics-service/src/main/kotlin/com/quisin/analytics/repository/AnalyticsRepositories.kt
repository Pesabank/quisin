package com.quisin.analytics.repository

import com.quisin.analytics.domain.AnalyticsCategory
import com.quisin.analytics.domain.CustomerAnalytics
import com.quisin.analytics.domain.RestaurantAnalytics
import com.quisin.analytics.dto.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface RestaurantAnalyticsRepository : JpaRepository<RestaurantAnalytics, UUID> {
    fun findByRestaurantIdAndCategory(restaurantId: UUID, category: AnalyticsCategory): List<RestaurantAnalytics>

    @Query("SELECT r FROM RestaurantAnalytics r WHERE r.restaurantId = :restaurantId AND r.createdAt BETWEEN :startDate AND :endDate")
    fun findByRestaurantIdAndDateRange(
        restaurantId: UUID, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<RestaurantAnalytics>
}

@Repository
interface CustomerAnalyticsRepository : JpaRepository<CustomerAnalytics, UUID> {
    fun findByCustomerIdAndRestaurantId(customerId: UUID, restaurantId: UUID): CustomerAnalytics?

    @Query("SELECT c FROM CustomerAnalytics c WHERE c.restaurantId = :restaurantId ORDER BY c.totalSpend DESC LIMIT 10")
    fun findTopCustomersBySpend(restaurantId: UUID): List<CustomerAnalytics>
}

@Repository
interface RevenueAnalyticsRepository : JpaRepository<RestaurantAnalytics, UUID> {
    @Query("""
        SELECT new map(
            SUM(ra.totalRevenue) as totalRevenue,
            ra.restaurantId as restaurantId
        )
        FROM RestaurantAnalytics ra
        WHERE ra.createdAt BETWEEN :startDate AND :endDate
        GROUP BY ra.restaurantId
    """)
    fun getRevenueByRestaurant(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Map<String, Any>>

    @Query("""
        SELECT new com.quisin.analytics.dto.RestaurantPerformance(
            ra.restaurantId, 
            'Restaurant Name', 
            SUM(ra.totalRevenue), 
            SUM(ra.totalOrders),
            AVG(ra.averageOrderValue)
        )
        FROM RestaurantAnalytics ra
        WHERE ra.createdAt BETWEEN :startDate AND :endDate
        GROUP BY ra.restaurantId
        ORDER BY SUM(ra.totalRevenue) DESC
        LIMIT 10
    """)
    fun getTopPerformingRestaurantsByRevenue(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<RestaurantPerformance>
}

@Repository
interface ChainRevenueAnalyticsRepository : JpaRepository<RestaurantAnalytics, UUID> {
    @Query("""
        SELECT new map(
            SUM(ra.totalRevenue) as totalRevenue,
            ra.chainId as chainId
        )
        FROM RestaurantAnalytics ra
        WHERE ra.createdAt BETWEEN :startDate AND :endDate
        GROUP BY ra.chainId
    """)
    fun getRevenueByChain(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Map<String, Any>>
}

@Repository
interface MenuItemSalesRepository : JpaRepository<RestaurantAnalytics, UUID> {
    @Query("""
        SELECT new com.quisin.analytics.dto.MenuItemPerformance(
            mi.id,
            mi.name,
            mc.name,
            SUM(oi.quantity),
            SUM(oi.totalPrice),
            AVG(mi.price)
        )
        FROM MenuItem mi
        JOIN mi.menuCategory mc
        JOIN OrderItem oi ON oi.menuItem.id = mi.id
        JOIN Order o ON o.id = oi.order.id
        WHERE 
            (:restaurantId IS NULL OR o.restaurant.id = :restaurantId) AND
            (:chainId IS NULL OR o.restaurant.chain.id = :chainId) AND
            o.orderDate BETWEEN :startDate AND :endDate
        GROUP BY mi.id, mi.name, mc.name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT :limit
    """)
    fun getTopSellingMenuItems(
        @Param("restaurantId") restaurantId: UUID?,
        @Param("chainId") chainId: UUID?,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        @Param("limit") limit: Int = 10
    ): List<MenuItemPerformance>

    @Query("""
        SELECT SUM(oi.quantity)
        FROM OrderItem oi
        JOIN Order o ON o.id = oi.order.id
        WHERE 
            (:restaurantId IS NULL OR o.restaurant.id = :restaurantId) AND
            (:chainId IS NULL OR o.restaurant.chain.id = :chainId) AND
            o.orderDate BETWEEN :startDate AND :endDate
    """)
    fun getTotalItemsSold(
        @Param("restaurantId") restaurantId: UUID?,
        @Param("chainId") chainId: UUID?,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Int

    @Query("""
        SELECT SUM(oi.totalPrice)
        FROM OrderItem oi
        JOIN Order o ON o.id = oi.order.id
        WHERE 
            (:restaurantId IS NULL OR o.restaurant.id = :restaurantId) AND
            (:chainId IS NULL OR o.restaurant.chain.id = :chainId) AND
            o.orderDate BETWEEN :startDate AND :endDate
    """)
    fun getTotalItemRevenue(
        @Param("restaurantId") restaurantId: UUID?,
        @Param("chainId") chainId: UUID?,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): BigDecimal
}
