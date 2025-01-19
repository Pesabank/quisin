package com.quisin.restaurant.service

import com.quisin.restaurant.client.NotificationRequest
import com.quisin.restaurant.client.NotificationServiceClient
import com.quisin.restaurant.client.NotificationType
import com.quisin.restaurant.event.*
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation

@Service
class EventListenerService(
    private val notificationServiceClient: NotificationServiceClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>
) {

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleRestaurantStatusChanged(event: RestaurantStatusChangedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.RESTAURANT_STATUS_CHANGED,
            title = "Restaurant Status Changed",
            message = "Your restaurant's status has been updated from ${event.oldStatus} to ${event.newStatus}",
            metadata = mapOf(
                "restaurantId" to event.restaurantId.toString(),
                "oldStatus" to event.oldStatus.toString(),
                "newStatus" to event.newStatus.toString()
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleRestaurantCreated(event: RestaurantCreatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.RESTAURANT_CREATED,
            title = "Restaurant Created",
            message = "Your restaurant '${event.name}' has been created successfully",
            metadata = mapOf(
                "restaurantId" to event.restaurantId.toString(),
                "chainId" to (event.chainId?.toString() ?: "")
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleRestaurantUpdated(event: RestaurantUpdatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.RESTAURANT_UPDATED,
            title = "Restaurant Updated",
            message = "Your restaurant has been updated successfully",
            metadata = mapOf(
                "restaurantId" to event.restaurantId.toString(),
                "name" to (event.name ?: ""),
                "description" to (event.description ?: "")
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleRestaurantDeleted(event: RestaurantDeletedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.RESTAURANT_DELETED,
            title = "Restaurant Deleted",
            message = "Your restaurant has been deleted successfully",
            metadata = mapOf(
                "restaurantId" to event.restaurantId.toString(),
                "chainId" to (event.chainId?.toString() ?: "")
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleChainCreated(event: ChainCreatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.CHAIN_CREATED,
            title = "Restaurant Chain Created",
            message = "Your restaurant chain '${event.name}' has been created successfully",
            metadata = mapOf(
                "chainId" to event.chainId.toString()
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleChainUpdated(event: ChainUpdatedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.CHAIN_UPDATED,
            title = "Restaurant Chain Updated",
            message = "Your restaurant chain has been updated successfully",
            metadata = mapOf(
                "chainId" to event.chainId.toString(),
                "name" to event.name,
                "description" to (event.description ?: "")
            )
        )

        sendNotification(notificationRequest)
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleChainDeleted(event: ChainDeletedEvent) {
        val notificationRequest = NotificationRequest(
            userId = event.ownerId,
            type = NotificationType.CHAIN_DELETED,
            title = "Restaurant Chain Deleted",
            message = "Your restaurant chain has been deleted successfully",
            metadata = mapOf(
                "chainId" to event.chainId.toString(),
                "restaurantIds" to event.restaurantIds.joinToString(",")
            )
        )

        sendNotification(notificationRequest)
    }

    private fun sendNotification(request: NotificationRequest) {
        circuitBreakerFactory.create("sendNotification").run({
            notificationServiceClient.sendNotification(request)
        }, { throwable ->
            // Log the error but don't throw - notification failures shouldn't affect the main flow
            println("Failed to send notification: ${throwable.message}")
        })
    }
} 