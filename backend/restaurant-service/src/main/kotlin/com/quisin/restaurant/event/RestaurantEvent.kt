package com.quisin.restaurant.event

import com.quisin.restaurant.domain.RestaurantStatus
import java.time.LocalDateTime
import java.util.UUID

sealed class RestaurantEvent {
    abstract val restaurantId: UUID
    abstract val timestamp: LocalDateTime
}

data class RestaurantStatusChangedEvent(
    override val restaurantId: UUID,
    val oldStatus: RestaurantStatus,
    val newStatus: RestaurantStatus,
    val ownerId: UUID,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : RestaurantEvent()

data class RestaurantCreatedEvent(
    override val restaurantId: UUID,
    val name: String,
    val ownerId: UUID,
    val chainId: UUID?,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : RestaurantEvent()

data class RestaurantUpdatedEvent(
    override val restaurantId: UUID,
    val name: String?,
    val description: String?,
    val ownerId: UUID,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : RestaurantEvent()

data class RestaurantDeletedEvent(
    override val restaurantId: UUID,
    val ownerId: UUID,
    val chainId: UUID?,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : RestaurantEvent() 