package com.quisin.analytics.controller

import com.quisin.analytics.domain.AnalyticsCategory
import com.quisin.analytics.dto.*
import com.quisin.analytics.service.AnalyticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics Management", description = "APIs for generating and retrieving restaurant and customer analytics")
class AnalyticsController(private val analyticsService: AnalyticsService) {

    @PostMapping("/restaurant")
    @Operation(summary = "Generate restaurant analytics", description = "Create analytics for a specific restaurant")
    fun generateRestaurantAnalytics(
        @RequestBody request: RestaurantAnalyticsRequest
    ): ResponseEntity<RestaurantAnalyticsResponse> {
        val response = analyticsService.generateRestaurantAnalytics(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/customer")
    @Operation(summary = "Generate customer analytics", description = "Create analytics for a specific customer at a restaurant")
    fun generateCustomerAnalytics(
        @RequestBody request: CustomerAnalyticsRequest
    ): ResponseEntity<CustomerAnalyticsResponse> {
        val response = analyticsService.generateCustomerAnalytics(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get restaurant analytics", description = "Retrieve analytics for a specific restaurant")
    fun getRestaurantAnalytics(
        @PathVariable restaurantId: UUID,
        @RequestParam(required = false) category: AnalyticsCategory?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?
    ): ResponseEntity<List<RestaurantAnalyticsResponse>> {
        val analytics = analyticsService.getRestaurantAnalytics(restaurantId, category, startDate, endDate)
        return ResponseEntity.ok(analytics)
    }

    @GetMapping("/customer/{customerId}/restaurant/{restaurantId}")
    @Operation(summary = "Get customer analytics", description = "Retrieve analytics for a specific customer at a restaurant")
    fun getCustomerAnalytics(
        @PathVariable customerId: UUID,
        @PathVariable restaurantId: UUID
    ): ResponseEntity<CustomerAnalyticsResponse?> {
        val analytics = analyticsService.getCustomerAnalytics(customerId, restaurantId)
        return ResponseEntity.ok(analytics)
    }

    @GetMapping("/restaurant/{restaurantId}/top-customers")
    @Operation(summary = "Get top customers by spend", description = "Retrieve top customers for a restaurant based on total spend")
    fun getTopCustomersBySpend(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<CustomerAnalyticsResponse>> {
        val topCustomers = analyticsService.getTopCustomersBySpend(restaurantId)
        return ResponseEntity.ok(topCustomers)
    }
}

@RestController
@RequestMapping("/api/v1/revenue")
@Tag(name = "Revenue Analytics", description = "APIs for tracking and analyzing revenue")
class RevenueAnalyticsController(
    private val revenueAnalyticsService: RevenueAnalyticsService
) {
    @PostMapping("/breakdown")
    @Operation(summary = "Get revenue breakdown", description = "Retrieve detailed revenue breakdown")
    fun getRevenueBreakdown(
        @RequestBody request: RevenueTrackingRequest
    ): ResponseEntity<RevenueBreakdownResponse> {
        val breakdown = revenueAnalyticsService.getRevenueBreakdown(request)
        return ResponseEntity.ok(breakdown)
    }

    @GetMapping("/top-restaurants")
    @Operation(summary = "Get top performing restaurants", description = "Retrieve top restaurants by revenue")
    fun getTopPerformingRestaurants(
        @RequestParam period: RevenuePeriod = RevenuePeriod.THIS_MONTH,
        @RequestParam(required = false) chainId: UUID?
    ): ResponseEntity<List<RestaurantPerformance>> {
        val topRestaurants = revenueAnalyticsService.getTopPerformingRestaurants(period, chainId)
        return ResponseEntity.ok(topRestaurants)
    }
}

@RestController
@RequestMapping("/api/v1/menu-sales")
@Tag(name = "Menu Item Sales Analytics", description = "APIs for tracking menu item sales")
class MenuItemSalesController(
    private val menuItemSalesService: MenuItemSalesService
) {
    @PostMapping("/top-items")
    @Operation(summary = "Get top selling menu items", description = "Retrieve top selling menu items")
    fun getTopSellingMenuItems(
        @RequestBody request: MenuItemSalesRequest
    ): ResponseEntity<MenuItemSalesResponse> {
        val salesResponse = menuItemSalesService.getMenuItemSales(request)
        return ResponseEntity.ok(salesResponse)
    }

    @PostMapping("/top-items-by-category")
    @Operation(summary = "Get top selling menu items by category", description = "Retrieve top selling menu items grouped by category")
    fun getTopSellingMenuItemsByCategory(
        @RequestBody request: MenuItemSalesRequest
    ): ResponseEntity<Map<String, List<MenuItemPerformance>>> {
        val categorizedSales = menuItemSalesService.getTopSellingMenuItemsByCategory(request)
        return ResponseEntity.ok(categorizedSales)
    }
}
