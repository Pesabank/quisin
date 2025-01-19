package com.quisin.notification.dto

import com.quisin.notification.domain.NotificationChannel
import com.quisin.notification.domain.NotificationType
import com.quisin.notification.domain.NotificationStatus
import java.time.LocalDateTime
import java.util.UUID

data class CreateNotificationRequest(
    val recipientId: UUID,
    val recipientEmail: String? = null,
    val recipientPhone: String? = null,
    val type: NotificationType,
    val channel: NotificationChannel,
    val message: String,
    val title: String? = null,
    val additionalDetails: String? = null,
    val relatedEntityId: UUID? = null,
    val relatedEntityType: String? = null
)

data class NotificationResponse(
    val id: UUID?,
    val recipientId: UUID,
    val type: NotificationType,
    val channel: NotificationChannel,
    val message: String,
    val title: String?,
    val status: NotificationStatus,
    val createdAt: LocalDateTime?,
    val sentAt: LocalDateTime?
)

data class NotificationPreferenceRequest(
    val userId: UUID,
    val enabledNotificationTypes: Set<NotificationType>? = null,
    val preferredChannels: Set<NotificationChannel>? = null,
    val receivePromotional: Boolean? = null,
    val receiveSystemAlerts: Boolean? = null
)

data class NotificationPreferenceResponse(
    val userId: UUID,
    val enabledNotificationTypes: Set<NotificationType>,
    val preferredChannels: Set<NotificationChannel>,
    val receivePromotional: Boolean,
    val receiveSystemAlerts: Boolean
)

data class SendBulkNotificationRequest(
    val recipientIds: List<UUID>,
    val type: NotificationType,
    val channels: Set<NotificationChannel>,
    val message: String,
    val title: String? = null
)
