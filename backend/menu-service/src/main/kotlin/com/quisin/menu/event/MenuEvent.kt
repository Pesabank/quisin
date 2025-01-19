package com.quisin.menu.event

import java.time.LocalDateTime

sealed class MenuEvent {
    abstract val menuId: String
    abstract val timestamp: LocalDateTime
}

data class MenuCreatedEvent(
    override val menuId: String,
    val name: String,
    val restaurantId: String,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuUpdatedEvent(
    override val menuId: String,
    val name: String?,
    val description: String?,
    val active: Boolean?,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuDeletedEvent(
    override val menuId: String,
    val restaurantId: String,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuItemCreatedEvent(
    override val menuId: String,
    val itemId: String,
    val name: String,
    val price: java.math.BigDecimal,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuItemUpdatedEvent(
    override val menuId: String,
    val itemId: String,
    val name: String?,
    val price: java.math.BigDecimal?,
    val available: Boolean?,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuItemDeletedEvent(
    override val menuId: String,
    val itemId: String,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuCategoryCreatedEvent(
    override val menuId: String,
    val categoryId: String,
    val name: String,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuCategoryUpdatedEvent(
    override val menuId: String,
    val categoryId: String,
    val name: String?,
    val active: Boolean?,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent()

data class MenuCategoryDeletedEvent(
    override val menuId: String,
    val categoryId: String,
    override val timestamp: LocalDateTime = LocalDateTime.now()
) : MenuEvent() 