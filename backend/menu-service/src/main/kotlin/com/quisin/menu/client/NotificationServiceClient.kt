package com.quisin.menu.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "notification-service", url = "\${quisin.services.notification-service.url}")
interface NotificationServiceClient {

    @PostMapping("/api/v1/notifications")
    fun sendNotification(@RequestBody request: NotificationRequest): NotificationResponse
}

data class NotificationRequest(
    val userId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val metadata: Map<String, String> = emptyMap()
)

data class NotificationResponse(
    val id: String,
    val status: NotificationStatus,
    val error: String?
)

enum class NotificationType {
    MENU_CREATED,
    MENU_UPDATED,
    MENU_DELETED,
    MENU_ITEM_CREATED,
    MENU_ITEM_UPDATED,
    MENU_ITEM_DELETED,
    MENU_CATEGORY_CREATED,
    MENU_CATEGORY_UPDATED,
    MENU_CATEGORY_DELETED
}

enum class NotificationStatus {
    SENT,
    FAILED,
    PENDING
} 