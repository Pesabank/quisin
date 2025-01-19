package com.quisin.restaurant.event

import java.time.LocalDateTime
import java.util.UUID

sealed class ChainEvent {
    abstract val chainId: UUID
    abstract val timestamp: LocalDateTime
}

data class ChainCreatedEvent(
    override val chainId: UUID,
    val name: String,
    val ownerId: UUID,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : ChainEvent()

data class ChainUpdatedEvent(
    override val chainId: UUID,
    val name: String,
    val description: String?,
    val ownerId: UUID,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : ChainEvent()

data class ChainDeletedEvent(
    override val chainId: UUID,
    val ownerId: UUID,
    val restaurantIds: Set<UUID>,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : ChainEvent() 