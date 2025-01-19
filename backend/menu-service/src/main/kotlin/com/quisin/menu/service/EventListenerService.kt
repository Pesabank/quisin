package com.quisin.menu.service

import com.quisin.menu.client.NotificationRequest
import com.quisin.menu.client.NotificationServiceClient
import com.quisin.menu.client.NotificationType
import com.quisin.menu.event.*
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener

@Service
class EventListenerService(
    private val notificationServiceClient: NotificationServiceClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>
) {

    @TransactionalEventListener
    @Transactional
    fun handleMenuCreated(event: MenuCreatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.restaurantId, // Restaurant owner will receive the notification
            type = NotificationType.MENU_CREATED,
            title = "Menu Created",
            message = "Menu '${event.name}' has been created successfully",
            metadata = mapOf(
                "menuId" to event.menuId,
                "restaurantId" to event.restaurantId
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional
    fun handleMenuUpdated(event: MenuUpdatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.menuId, // We'll get the restaurant owner ID from the menu
            type = NotificationType.MENU_UPDATED,
            title = "Menu Updated",
            message = "Menu has been updated successfully",
            metadata = mapOf(
                "menuId" to event.menuId,
                "name" to (event.name ?: "unchanged"),
                "description" to (event.description ?: "unchanged"),
                "active" to (event.active?.toString() ?: "unchanged")
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional
    fun handleMenuDeleted(event: MenuDeletedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.restaurantId,
            type = NotificationType.MENU_DELETED,
            title = "Menu Deleted",
            message = "Menu has been deleted successfully",
            metadata = mapOf(
                "menuId" to event.menuId,
                "restaurantId" to event.restaurantId
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional
    fun handleMenuItemCreated(event: MenuItemCreatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.menuId, // We'll get the restaurant owner ID from the menu
            type = NotificationType.MENU_ITEM_CREATED,
            title = "Menu Item Created",
            message = "Menu item '${event.name}' has been created successfully",
            metadata = mapOf(
                "menuId" to event.menuId,
                "itemId" to event.itemId,
                "price" to event.price.toString()
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional
    fun handleMenuItemUpdated(event: MenuItemUpdatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.menuId,
            type = NotificationType.MENU_ITEM_UPDATED,
            title = "Menu Item Updated",
            message = "Menu item has been updated successfully",
            metadata = mapOf(
                "menuId" to event.menuId,
                "itemId" to event.itemId,
                "name" to (event.name ?: "unchanged"),
                "price" to (event.price?.toString() ?: "unchanged"),
                "available" to (event.available?.toString() ?: "unchanged")
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional
    fun handleMenuItemDeleted(event: MenuItemDeletedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.menuId,
            type = NotificationType.MENU_ITEM_DELETED,
            title = "Menu Item Deleted",
            message = "Menu item has been deleted successfully",
            metadata = mapOf(
                "menuId" to event.menuId,
                "itemId" to event.itemId
            )
        )

        sendNotification(notificationRequest)
    }

    private fun sendNotification(request: NotificationRequest) {
        circuitBreakerFactory.create("sendNotification").run({
            notificationServiceClient.sendNotification(request)
        }, { throwable ->
            // Log the error but don't throw - notifications are not critical
            println("Failed to send notification: ${throwable.message}")
        })
    }
} 