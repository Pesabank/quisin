package com.quisin.notification.service

import com.quisin.notification.domain.*
import com.quisin.notification.dto.*
import com.quisin.notification.repository.NotificationPreferenceRepository
import com.quisin.notification.repository.NotificationRepository
import com.quisin.restaurant.domain.Table
import com.quisin.restaurant.domain.TableRepository
import com.quisin.restaurant.domain.Restaurant
import com.quisin.restaurant.domain.RestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val emailService: EmailNotificationService,
    private val smsService: SMSNotificationService,
    private val pushNotificationService: PushNotificationService,
    private val tableRepository: TableRepository,
    private val restaurantRepository: RestaurantRepository
) {
    @Transactional
    fun createNotification(request: CreateNotificationRequest): NotificationResponse {
        // Check user notification preferences
        val preferences = notificationPreferenceRepository.findByUserId(request.recipientId)
        
        // Validate if notification type and channel are enabled
        if (preferences != null) {
            if (!preferences.enabledNotificationTypes.contains(request.type) ||
                !preferences.preferredChannels.contains(request.channel)) {
                throw NotificationPreferenceException("Notification type or channel is not enabled")
            }
        }

        // Create notification entity
        val notification = Notification(
            recipientId = request.recipientId,
            recipientEmail = request.recipientEmail,
            recipientPhone = request.recipientPhone,
            type = request.type,
            channel = request.channel,
            message = request.message,
            title = request.title,
            additionalDetails = request.additionalDetails,
            relatedEntityId = request.relatedEntityId,
            relatedEntityType = request.relatedEntityType
        )

        val savedNotification = notificationRepository.save(notification)

        // Send notification based on channel
        sendNotification(savedNotification)

        return mapToNotificationResponse(savedNotification)
    }

    @Transactional
    fun sendBulkNotification(request: SendBulkNotificationRequest) {
        request.recipientIds.forEach { recipientId ->
            val bulkRequest = CreateNotificationRequest(
                recipientId = recipientId,
                type = request.type,
                channel = request.channels.first(), // For simplicity, using first channel
                message = request.message,
                title = request.title
            )
            createNotification(bulkRequest)
        }
    }

    private fun sendNotification(notification: Notification) {
        try {
            when (notification.channel) {
                NotificationChannel.EMAIL -> 
                    emailService.sendEmail(notification)
                NotificationChannel.SMS -> 
                    smsService.sendSMS(notification)
                NotificationChannel.PUSH_NOTIFICATION -> 
                    pushNotificationService.sendPushNotification(notification)
                NotificationChannel.IN_APP -> 
                    saveInAppNotification(notification)
            }
            
            // Update notification status
            notification.status = NotificationStatus.SENT
            notification.sentAt = LocalDateTime.now()
            notificationRepository.save(notification)
        } catch (e: Exception) {
            // Handle notification sending failure
            notification.status = NotificationStatus.FAILED
            notificationRepository.save(notification)
            throw NotificationSendingException("Failed to send notification: ${e.message}")
        }
    }

    private fun saveInAppNotification(notification: Notification) {
        // For in-app notifications, just save to database
        // Frontend will query and display these notifications
    }

    @Transactional
    fun updateNotificationPreferences(request: NotificationPreferenceRequest): NotificationPreferenceResponse {
        val existingPreferences = notificationPreferenceRepository.findByUserId(request.userId)
            ?: NotificationPreference(userId = request.userId)

        val updatedPreferences = existingPreferences.copy(
            enabledNotificationTypes = request.enabledNotificationTypes 
                ?: existingPreferences.enabledNotificationTypes,
            preferredChannels = request.preferredChannels 
                ?: existingPreferences.preferredChannels,
            receivePromotional = request.receivePromotional 
                ?: existingPreferences.receivePromotional,
            receiveSystemAlerts = request.receiveSystemAlerts 
                ?: existingPreferences.receiveSystemAlerts
        )

        val savedPreferences = notificationPreferenceRepository.save(updatedPreferences)

        return NotificationPreferenceResponse(
            userId = savedPreferences.userId,
            enabledNotificationTypes = savedPreferences.enabledNotificationTypes,
            preferredChannels = savedPreferences.preferredChannels,
            receivePromotional = savedPreferences.receivePromotional,
            receiveSystemAlerts = savedPreferences.receiveSystemAlerts
        )
    }

    fun getUserNotifications(userId: UUID, status: NotificationStatus? = null): List<NotificationResponse> {
        val notifications = status?.let { 
            notificationRepository.findByRecipientIdAndStatus(userId, it)
        } ?: notificationRepository.findByRecipientId(userId)

        return notifications.map { mapToNotificationResponse(it) }
    }

    private fun mapToNotificationResponse(notification: Notification): NotificationResponse {
        return NotificationResponse(
            id = notification.id,
            recipientId = notification.recipientId,
            type = notification.type,
            channel = notification.channel,
            message = notification.message,
            title = notification.title,
            status = notification.status,
            createdAt = notification.createdAt,
            sentAt = notification.sentAt
        )
    }

    fun hailWaiter(
        tableId: UUID, 
        restaurantId: UUID, 
        userId: UUID, 
        reason: String? = null
    ): NotificationResponse {
        // Validate table and restaurant
        val table = tableRepository.findByIdOrNull(tableId) 
            ?: throw NotFoundException("Table not found")
        
        val restaurant = restaurantRepository.findByIdOrNull(restaurantId)
            ?: throw NotFoundException("Restaurant not found")
        
        // Create notification for staff
        val staffNotification = CreateNotificationRequest(
            recipientId = restaurant.managerId, // Assuming restaurant has a manager ID
            type = NotificationType.HAIL_WAITER,
            channel = NotificationChannel.IN_APP,
            message = "Waiter requested at Table ${table.tableNumber} in ${restaurant.name}. " + 
                      (reason?.let { "Reason: $it" } ?: ""),
            metadata = mapOf(
                "tableId" to tableId.toString(),
                "userId" to userId.toString(),
                "restaurantId" to restaurantId.toString()
            )
        )

        // Send notification and return response
        return createNotification(staffNotification)
    }

    fun resolveWaiterRequest(notificationId: UUID) {
        val notification = notificationRepository.findByIdOrNull(notificationId)
            ?: throw NotFoundException("Notification not found")
        
        notification.status = NotificationStatus.RESOLVED
        notificationRepository.save(notification)
    }
}

// Placeholder services for different notification channels
@Service
class EmailNotificationService {
    fun sendEmail(notification: Notification) {
        // Implement email sending logic
        // Could use JavaMailSender or external email service
    }
}

@Service
class SMSNotificationService {
    fun sendSMS(notification: Notification) {
        // Implement SMS sending logic
        // Could use Twilio or other SMS gateway
    }
}

@Service
class PushNotificationService {
    fun sendPushNotification(notification: Notification) {
        // Implement push notification logic
        // Could use Firebase Cloud Messaging or similar service
    }
}

// Custom Exceptions
class NotificationPreferenceException(message: String) : RuntimeException(message)
class NotificationSendingException(message: String) : RuntimeException(message)
