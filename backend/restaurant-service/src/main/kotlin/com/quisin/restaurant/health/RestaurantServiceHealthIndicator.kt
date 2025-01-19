package com.quisin.restaurant.health

import com.quisin.restaurant.repository.RestaurantRepository
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class RestaurantServiceHealthIndicator(
    private val restaurantRepository: RestaurantRepository
) : HealthIndicator {

    override fun health(): Health {
        return try {
            val count = restaurantRepository.count()
            Health.up()
                .withDetail("totalRestaurants", count)
                .build()
        } catch (e: Exception) {
            Health.down()
                .withException(e)
                .build()
        }
    }
} 