package com.quisin.analytics.service

import com.quisin.analytics.domain.AnalyticsCategory
import com.quisin.analytics.domain.CustomerAnalytics
import com.quisin.analytics.domain.RestaurantAnalytics
import com.quisin.analytics.dto.*
import com.quisin.analytics.repository.CustomerAnalyticsRepository
import com.quisin.analytics.repository.RestaurantAnalyticsRepository
import com.quisin.analytics.repository.RevenueAnalyticsRepository
import com.quisin.analytics.repository.ChainRevenueAnalyticsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AnalyticsService(
    private val restaurantAnalyticsRepository: RestaurantAnalyticsRepository,
    private val customerAnalyticsRepository: CustomerAnalyticsRepository
) {
    @Transactional
    fun generateRestaurantAnalytics(request: RestaurantAnalyticsRequest): RestaurantAnalyticsResponse {
        // Placeholder for actual analytics generation logic
        val analytics = RestaurantAnalytics(
            restaurantId = request.restaurantId,
            category = request.category,
            totalRevenue = java.math.BigDecimal.ZERO,
            totalOrders = 0,
            uniqueCustomers = 0,
            averageOrderValue = java.math.BigDecimal.ZERO
        )
        val savedAnalytics = restaurantAnalyticsRepository.save(analytics)
        
        return RestaurantAnalyticsResponse(
            id = savedAnalytics.id,
            restaurantId = savedAnalytics.restaurantId,
            category = savedAnalytics.category,
            totalRevenue = savedAnalytics.totalRevenue,
            totalOrders = savedAnalytics.totalOrders,
            uniqueCustomers = savedAnalytics.uniqueCustomers,
            averageOrderValue = savedAnalytics.averageOrderValue,
            topSellingItem = savedAnalytics.topSellingItem,
            peakHour = savedAnalytics.peakHour,
            revenueBreakdown = savedAnalytics.revenueBreakdown,
            orderDistribution = savedAnalytics.orderDistribution
        )
    }

    @Transactional
    fun generateCustomerAnalytics(request: CustomerAnalyticsRequest): CustomerAnalyticsResponse {
        // Placeholder for actual customer analytics generation logic
        val analytics = CustomerAnalytics(
            customerId = request.customerId,
            restaurantId = request.restaurantId,
            totalVisits = 0,
            totalSpend = java.math.BigDecimal.ZERO,
            averageSpendPerVisit = java.math.BigDecimal.ZERO
        )
        val savedAnalytics = customerAnalyticsRepository.save(analytics)
        
        return CustomerAnalyticsResponse(
            id = savedAnalytics.id,
            customerId = savedAnalytics.customerId,
            restaurantId = savedAnalytics.restaurantId,
            totalVisits = savedAnalytics.totalVisits,
            totalSpend = savedAnalytics.totalSpend,
            averageSpendPerVisit = savedAnalytics.averageSpendPerVisit,
            favoriteItems = savedAnalytics.favoriteItems,
            lastVisitDate = savedAnalytics.lastVisitDate
        )
    }

    fun getRestaurantAnalytics(
        restaurantId: UUID, 
        category: AnalyticsCategory? = null,
        startDate: LocalDateTime? = null, 
        endDate: LocalDateTime? = null
    ): List<RestaurantAnalyticsResponse> {
        val analytics = when {
            category != null && startDate != null && endDate != null -> 
                restaurantAnalyticsRepository.findByRestaurantIdAndDateRange(restaurantId, startDate, endDate)
            category != null -> 
                restaurantAnalyticsRepository.findByRestaurantIdAndCategory(restaurantId, category)
            else -> 
                restaurantAnalyticsRepository.findAll().filter { it.restaurantId == restaurantId }
        }

        return analytics.map { 
            RestaurantAnalyticsResponse(
                id = it.id,
                restaurantId = it.restaurantId,
                category = it.category,
                totalRevenue = it.totalRevenue,
                totalOrders = it.totalOrders,
                uniqueCustomers = it.uniqueCustomers,
                averageOrderValue = it.averageOrderValue,
                topSellingItem = it.topSellingItem,
                peakHour = it.peakHour,
                revenueBreakdown = it.revenueBreakdown,
                orderDistribution = it.orderDistribution
            )
        }
    }

    fun getCustomerAnalytics(
        customerId: UUID, 
        restaurantId: UUID
    ): CustomerAnalyticsResponse? {
        val analytics = customerAnalyticsRepository.findByCustomerIdAndRestaurantId(customerId, restaurantId)
        
        return analytics?.let { 
            CustomerAnalyticsResponse(
                id = it.id,
                customerId = it.customerId,
                restaurantId = it.restaurantId,
                totalVisits = it.totalVisits,
                totalSpend = it.totalSpend,
                averageSpendPerVisit = it.averageSpendPerVisit,
                favoriteItems = it.favoriteItems,
                lastVisitDate = it.lastVisitDate
            )
        }
    }

    fun getTopCustomersBySpend(restaurantId: UUID): List<CustomerAnalyticsResponse> {
        return customerAnalyticsRepository.findTopCustomersBySpend(restaurantId)
            .map { 
                CustomerAnalyticsResponse(
                    id = it.id,
                    customerId = it.customerId,
                    restaurantId = it.restaurantId,
                    totalVisits = it.totalVisits,
                    totalSpend = it.totalSpend,
                    averageSpendPerVisit = it.averageSpendPerVisit,
                    favoriteItems = it.favoriteItems,
                    lastVisitDate = it.lastVisitDate
                )
            }
    }
}

@Service
class RevenueAnalyticsService(
    private val revenueAnalyticsRepository: RevenueAnalyticsRepository,
    private val chainRevenueAnalyticsRepository: ChainRevenueAnalyticsRepository
) {
    fun calculateRevenuePeriod(period: RevenuePeriod): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        return when (period) {
            RevenuePeriod.TODAY -> 
                Pair(now.toLocalDate().atStartOfDay(), now)
            RevenuePeriod.THIS_WEEK -> 
                Pair(now.toLocalDate().minusDays(now.dayOfWeek.value - 1L).atStartOfDay(), now)
            RevenuePeriod.THIS_MONTH -> 
                Pair(now.toLocalDate().withDayOfMonth(1).atStartOfDay(), now)
            RevenuePeriod.THIS_QUARTER -> {
                val firstMonthOfQuarter = ((now.monthValue - 1) / 3) * 3 + 1
                Pair(now.toLocalDate().withMonth(firstMonthOfQuarter).withDayOfMonth(1).atStartOfDay(), now)
            }
            RevenuePeriod.THIS_YEAR -> 
                Pair(now.toLocalDate().withDayOfYear(1).atStartOfDay(), now)
            RevenuePeriod.LAST_WEEK -> {
                val lastWeekStart = now.toLocalDate().minusDays(now.dayOfWeek.value + 6L)
                val lastWeekEnd = lastWeekStart.plusDays(6)
                Pair(lastWeekStart.atStartOfDay(), lastWeekEnd.atTime(23, 59, 59))
            }
            RevenuePeriod.LAST_MONTH -> {
                val lastMonth = now.minusMonths(1)
                Pair(lastMonth.toLocalDate().withDayOfMonth(1).atStartOfDay(), 
                     lastMonth.toLocalDate().withDayOfMonth(lastMonth.toLocalDate().lengthOfMonth()).atTime(23, 59, 59))
            }
            RevenuePeriod.LAST_QUARTER -> {
                val lastQuarterStart = now.minusMonths(3).withDayOfMonth(1)
                val lastQuarterEnd = lastQuarterStart.plusMonths(3).minusDays(1)
                Pair(lastQuarterStart.atStartOfDay(), lastQuarterEnd.atTime(23, 59, 59))
            }
            RevenuePeriod.LAST_YEAR -> {
                val lastYear = now.minusYears(1)
                Pair(lastYear.toLocalDate().withDayOfYear(1).atStartOfDay(), 
                     lastYear.toLocalDate().withDayOfYear(lastYear.toLocalDate().lengthOfYear()).atTime(23, 59, 59))
            }
            RevenuePeriod.CUSTOM -> throw IllegalArgumentException("Custom period requires explicit start and end dates")
        }
    }

    fun getRevenueBreakdown(request: RevenueTrackingRequest): RevenueBreakdownResponse {
        val (startDate, endDate) = when {
            request.startDate != null && request.endDate != null -> 
                Pair(request.startDate, request.endDate)
            request.period != RevenuePeriod.CUSTOM -> 
                calculateRevenuePeriod(request.period)
            else -> throw IllegalArgumentException("Either period or explicit dates must be provided")
        }

        return when (request.aggregationType) {
            RevenueAggregationType.RESTAURANT -> getRestaurantRevenue(startDate, endDate, request.restaurantId)
            RevenueAggregationType.CHAIN -> getChainRevenue(startDate, endDate, request.chainId)
            RevenueAggregationType.GLOBAL -> getGlobalRevenue(startDate, endDate)
        }
    }

    private fun getRestaurantRevenue(
        startDate: LocalDateTime, 
        endDate: LocalDateTime, 
        restaurantId: UUID?
    ): RevenueBreakdownResponse {
        val revenueData = revenueAnalyticsRepository.getRevenueByRestaurant(startDate, endDate)
        
        return RevenueBreakdownResponse(
            totalRevenue = revenueData.sumOf { it["totalRevenue"] as BigDecimal },
            periodStart = startDate,
            periodEnd = endDate,
            period = RevenuePeriod.CUSTOM,
            aggregationType = RevenueAggregationType.RESTAURANT,
            revenueByRestaurant = revenueData.associate { 
                (it["restaurantId"] as UUID) to (it["totalRevenue"] as BigDecimal) 
            }
        )
    }

    private fun getChainRevenue(
        startDate: LocalDateTime, 
        endDate: LocalDateTime, 
        chainId: UUID?
    ): RevenueBreakdownResponse {
        val revenueData = chainRevenueAnalyticsRepository.getRevenueByChain(startDate, endDate)
        
        return RevenueBreakdownResponse(
            totalRevenue = revenueData.sumOf { it["totalRevenue"] as BigDecimal },
            periodStart = startDate,
            periodEnd = endDate,
            period = RevenuePeriod.CUSTOM,
            aggregationType = RevenueAggregationType.CHAIN,
            revenueByChain = revenueData.associate { 
                (it["chainId"] as UUID) to (it["totalRevenue"] as BigDecimal) 
            }
        )
    }

    private fun getGlobalRevenue(
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): RevenueBreakdownResponse {
        val restaurantRevenue = revenueAnalyticsRepository.getRevenueByRestaurant(startDate, endDate)
        
        return RevenueBreakdownResponse(
            totalRevenue = restaurantRevenue.sumOf { it["totalRevenue"] as BigDecimal },
            periodStart = startDate,
            periodEnd = endDate,
            period = RevenuePeriod.CUSTOM,
            aggregationType = RevenueAggregationType.GLOBAL
        )
    }

    fun getTopPerformingRestaurants(period: RevenuePeriod, chainId: UUID? = null): List<RestaurantPerformance> {
        val (startDate, endDate) = calculateRevenuePeriod(period)
        return revenueAnalyticsRepository.getTopPerformingRestaurants(startDate, endDate)
    }
}
