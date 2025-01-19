package com.quisin.restaurant.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@FeignClient(name = "notification-service", url = "\${NOTIFICATION_SERVICE_URL:http://notification-service:8084}")
interface NotificationServiceClient {

    @PostMapping("/api/v1/notifications")
    fun sendNotification(@RequestBody request: NotificationRequest): NotificationResponse
}

data class NotificationRequest(
    val userId: UUID,
    val type: NotificationType,
    val title: String,
    val message: String,
    val metadata: Map<String, String> = emptyMap()
)

data class NotificationResponse(
    val id: UUID,
    val status: NotificationStatus,
    val error: String?
)

enum class NotificationType {
    RESTAURANT_STATUS_CHANGED,
    RESTAURANT_CREATED,
    RESTAURANT_UPDATED,
    RESTAURANT_DELETED,
    CHAIN_CREATED,
    CHAIN_UPDATED,
    CHAIN_DELETED,
    MENU_UPDATED,
    ORDER_RECEIVED,
    ORDER_STATUS_CHANGED
}

enum class NotificationStatus {
    SENT,
    FAILED,
    PENDING
} 