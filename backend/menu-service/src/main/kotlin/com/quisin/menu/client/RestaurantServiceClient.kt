package com.quisin.menu.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDateTime

@FeignClient(name = "restaurant-service", url = "\${quisin.services.restaurant-service.url}")
interface RestaurantServiceClient {

    @GetMapping("/api/v1/restaurants/{restaurantId}")
    fun getRestaurant(@PathVariable restaurantId: String): RestaurantResponse
}

data class RestaurantResponse(
    val id: String,
    val name: String,
    val description: String,
    val status: RestaurantStatus,
    val ownerId: String,
    val chainId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class RestaurantStatus {
    PENDING,
    ACTIVE,
    INACTIVE,
    SUSPENDED
} 