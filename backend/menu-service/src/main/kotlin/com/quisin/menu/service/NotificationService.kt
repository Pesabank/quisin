package com.quisin.menu.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.slf4j.LoggerFactory

@Service
class NotificationService(
    private val notificationRestTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMenuCreatedNotification(menuId: String, restaurantId: String) {
        try {
            val notification = NotificationRequest(
                type = "MENU_CREATED",
                targetId = menuId,
                restaurantId = restaurantId,
                message = "New menu has been created",
                priority = "NORMAL"
            )
            notificationRestTemplate.postForEntity("/api/v1/notifications", notification, Void::class.java)
            logger.info("Menu created notification sent for menuId: $menuId")
        } catch (e: Exception) {
            logger.error("Failed to send menu created notification for menuId: $menuId", e)
        }
    }

    fun sendMenuUpdatedNotification(menuId: String, restaurantId: String) {
        try {
            val notification = NotificationRequest(
                type = "MENU_UPDATED",
                targetId = menuId,
                restaurantId = restaurantId,
                message = "Menu has been updated",
                priority = "NORMAL"
            )
            notificationRestTemplate.postForEntity("/api/v1/notifications", notification, Void::class.java)
            logger.info("Menu updated notification sent for menuId: $menuId")
        } catch (e: Exception) {
            logger.error("Failed to send menu updated notification for menuId: $menuId", e)
        }
    }

    fun sendMenuDeletedNotification(menuId: String, restaurantId: String) {
        try {
            val notification = NotificationRequest(
                type = "MENU_DELETED",
                targetId = menuId,
                restaurantId = restaurantId,
                message = "Menu has been deleted",
                priority = "HIGH"
            )
            notificationRestTemplate.postForEntity("/api/v1/notifications", notification, Void::class.java)
            logger.info("Menu deleted notification sent for menuId: $menuId")
        } catch (e: Exception) {
            logger.error("Failed to send menu deleted notification for menuId: $menuId", e)
        }
    }
}

data class NotificationRequest(
    val type: String,
    val targetId: String,
    val restaurantId: String,
    val message: String,
    val priority: String
) 