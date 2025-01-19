package com.quisin.analytics.service

import com.quisin.analytics.dto.*
import com.quisin.analytics.repository.MenuItemSalesRepository
import com.quisin.analytics.service.RevenueAnalyticsService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
class MenuItemSalesService(
    private val menuItemSalesRepository: MenuItemSalesRepository,
    private val revenueAnalyticsService: RevenueAnalyticsService
) {
    fun getMenuItemSales(request: MenuItemSalesRequest): MenuItemSalesResponse {
        // Determine the date range
        val (startDate, endDate) = when {
            request.startDate != null && request.endDate != null -> 
                Pair(request.startDate, request.endDate)
            else -> 
                revenueAnalyticsService.calculateRevenuePeriod(request.period)
        }

        // Get top selling menu items
        val topSellingItems = menuItemSalesRepository.getTopSellingMenuItems(
            restaurantId = request.restaurantId,
            chainId = request.chainId,
            startDate = startDate,
            endDate = endDate,
            limit = request.limit
        )

        // Get total items sold and total revenue
        val totalItemsSold = menuItemSalesRepository.getTotalItemsSold(
            restaurantId = request.restaurantId,
            chainId = request.chainId,
            startDate = startDate,
            endDate = endDate
        ) ?: 0

        val totalItemRevenue = menuItemSalesRepository.getTotalItemRevenue(
            restaurantId = request.restaurantId,
            chainId = request.chainId,
            startDate = startDate,
            endDate = endDate
        ) ?: BigDecimal.ZERO

        return MenuItemSalesResponse(
            period = request.period,
            periodStart = startDate,
            periodEnd = endDate,
            topSellingItems = topSellingItems,
            totalItemsSold = totalItemsSold,
            totalItemRevenue = totalItemRevenue
        )
    }

    fun getTopSellingMenuItemsByCategory(request: MenuItemSalesRequest): Map<String, List<MenuItemPerformance>> {
        val salesResponse = getMenuItemSales(request)
        
        return salesResponse.topSellingItems
            .groupBy { it.categoryName ?: "Uncategorized" }
    }
}
