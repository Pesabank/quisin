package com.quisin.restaurant.service

import com.quisin.restaurant.domain.RestaurantStatus
import com.quisin.restaurant.event.RestaurantStatusChangedEvent
import com.quisin.restaurant.repository.RestaurantRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RestaurantActivationService(
    private val restaurantRepository: RestaurantRepository,
    private val eventPublisherService: EventPublisherService
) {
    fun toggleRestaurantStatus(
        restaurantId: UUID, 
        newStatus: RestaurantStatus
    ): Boolean {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { IllegalArgumentException("Restaurant not found") }

        val oldStatus = restaurant.status
        restaurant.status = newStatus
        restaurantRepository.save(restaurant)

        eventPublisherService.publishEvent(
            RestaurantStatusChangedEvent(
                restaurantId = restaurantId,
                ownerId = restaurant.ownerId,
                oldStatus = oldStatus,
                newStatus = newStatus
            )
        )

        return true
    }

    fun isRestaurantActive(restaurantId: UUID): Boolean {
        val restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow { IllegalArgumentException("Restaurant not found") }
        
        return restaurant.status == RestaurantStatus.ACTIVE
    }
}
