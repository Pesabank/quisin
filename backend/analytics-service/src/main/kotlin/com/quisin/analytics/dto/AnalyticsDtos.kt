package com.quisin.analytics.dto

import com.quisin.analytics.domain.AnalyticsCategory
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDate
import java.util.UUID

data class RestaurantAnalyticsRequest(
    val restaurantId: UUID,
    val category: AnalyticsCategory
)

data class RestaurantAnalyticsResponse(
    val id: UUID?,
    val restaurantId: UUID,
    val category: AnalyticsCategory,
    val totalRevenue: BigDecimal,
    val totalOrders: Int,
    val uniqueCustomers: Int,
    val averageOrderValue: BigDecimal,
    val topSellingItem: String?,
    val peakHour: LocalDateTime?,
    val revenueBreakdown: Map<String, BigDecimal>,
    val orderDistribution: Map<String, Int>
)

data class CustomerAnalyticsRequest(
    val customerId: UUID,
    val restaurantId: UUID
)

data class CustomerAnalyticsResponse(
    val id: UUID?,
    val customerId: UUID,
    val restaurantId: UUID,
    val totalVisits: Int,
    val totalSpend: BigDecimal,
    val averageSpendPerVisit: BigDecimal,
    val favoriteItems: Map<String, Int>,
    val lastVisitDate: LocalDateTime?
)

data class AnalyticsSummaryResponse(
    val restaurantAnalytics: List<RestaurantAnalyticsResponse>,
    val customerAnalytics: List<CustomerAnalyticsResponse>
)

enum class RevenuePeriod {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_QUARTER,
    THIS_YEAR,
    LAST_WEEK,
    LAST_MONTH,
    LAST_QUARTER,
    LAST_YEAR,
    CUSTOM
}

enum class RevenueAggregationType {
    RESTAURANT,
    CHAIN,
    GLOBAL
}

data class RevenueTrackingRequest(
    val period: RevenuePeriod,
    val aggregationType: RevenueAggregationType,
    val restaurantId: UUID? = null,
    val chainId: UUID? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)

data class RevenueBreakdownResponse(
    val totalRevenue: BigDecimal,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val period: RevenuePeriod,
    val aggregationType: RevenueAggregationType,
    val revenueByRestaurant: Map<UUID, BigDecimal> = emptyMap(),
    val revenueByChain: Map<UUID, BigDecimal> = emptyMap(),
    val dailyRevenue: Map<LocalDate, BigDecimal> = emptyMap(),
    val hourlyRevenue: Map<LocalDateTime, BigDecimal> = emptyMap()
)

data class TopPerformingRestaurantsResponse(
    val chainId: UUID? = null,
    val topRestaurants: List<RestaurantPerformance>
)

data class RestaurantPerformance(
    val restaurantId: UUID,
    val restaurantName: String,
    val totalRevenue: BigDecimal,
    val orderCount: Int,
    val averageOrderValue: BigDecimal
)

data class MenuItemSalesRequest(
    val restaurantId: UUID? = null,
    val chainId: UUID? = null,
    val period: RevenuePeriod = RevenuePeriod.THIS_MONTH,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val limit: Int = 10
)

data class MenuItemPerformance(
    val menuItemId: UUID,
    val menuItemName: String,
    val categoryName: String? = null,
    val totalQuantitySold: Int,
    val totalRevenue: BigDecimal,
    val averagePrice: BigDecimal
)

data class MenuItemSalesResponse(
    val period: RevenuePeriod,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val topSellingItems: List<MenuItemPerformance>,
    val totalItemsSold: Int,
    val totalItemRevenue: BigDecimal
)
