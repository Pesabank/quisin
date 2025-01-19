package com.quisin.notification.controller

import com.quisin.notification.domain.NotificationStatus
import com.quisin.notification.dto.*
import com.quisin.notification.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Management", description = "APIs for managing notifications")
class NotificationController(private val notificationService: NotificationService) {

    @PostMapping
    @Operation(summary = "Create and send a notification", description = "Create a new notification and send it through the specified channel")
    fun createNotification(
        @RequestBody request: CreateNotificationRequest
    ): ResponseEntity<NotificationResponse> {
        val response = notificationService.createNotification(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/bulk")
    @Operation(summary = "Send bulk notifications", description = "Send notifications to multiple recipients")
    fun sendBulkNotification(
        @RequestBody request: SendBulkNotificationRequest
    ): ResponseEntity<Void> {
        notificationService.sendBulkNotification(request)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Retrieve notifications for a specific user")
    fun getUserNotifications(
        @PathVariable userId: UUID,
        @RequestParam(required = false) status: NotificationStatus?
    ): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationService.getUserNotifications(userId, status)
        return ResponseEntity.ok(notifications)
    }

    @PostMapping("/preferences")
    @Operation(summary = "Update notification preferences", description = "Update a user's notification preferences")
    fun updateNotificationPreferences(
        @RequestBody request: NotificationPreferenceRequest
    ): ResponseEntity<NotificationPreferenceResponse> {
        val response = notificationService.updateNotificationPreferences(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/hail-waiter")
    @Operation(summary = "Hail a waiter", description = "Request a waiter for a specific table")
    fun hailWaiter(
        @RequestParam tableId: UUID,
        @RequestParam restaurantId: UUID,
        @RequestParam userId: UUID,
        @RequestParam(required = false) reason: String?
    ): ResponseEntity<NotificationResponse> {
        val response = notificationService.hailWaiter(tableId, restaurantId, userId, reason)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{notificationId}/resolve")
    @Operation(summary = "Resolve a waiter request", description = "Mark a waiter request notification as resolved")
    fun resolveWaiterRequest(
        @PathVariable notificationId: UUID
    ): ResponseEntity<Void> {
        notificationService.resolveWaiterRequest(notificationId)
        return ResponseEntity.accepted().build()
    }
}
