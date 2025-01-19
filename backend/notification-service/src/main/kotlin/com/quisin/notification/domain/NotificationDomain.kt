package com.quisin.notification.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class NotificationType {
    RESERVATION_CONFIRMATION,
    RESERVATION_REMINDER,
    ORDER_STATUS,
    PROMOTIONAL,
    SYSTEM_ALERT,
    FEEDBACK_REQUEST,
    HAIL_WAITER,  // New specific type for waiter requests
    MENU_ITEM_AVAILABILITY
}

enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH_NOTIFICATION,
    IN_APP
}

enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    READ
}

@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener::class)
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val recipientId: UUID,

    @Column(nullable = false)
    val recipientEmail: String? = null,

    @Column(nullable = false)
    val recipientPhone: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val channel: NotificationChannel,

    @Column(nullable = false, length = 1000)
    val message: String,

    @Column(length = 500)
    val title: String? = null,

    @Column(length = 1000)
    val additionalDetails: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: NotificationStatus = NotificationStatus.PENDING,

    @Column
    val relatedEntityId: UUID? = null,

    @Column
    val relatedEntityType: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column
    val sentAt: LocalDateTime? = null,

    @Column
    val readAt: LocalDateTime? = null
)

@Entity
@Table(name = "notification_preferences")
data class NotificationPreference(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @ElementCollection
    @CollectionTable(name = "user_notification_types", joinColumns = [JoinColumn(name = "preference_id")])
    @Enumerated(EnumType.STRING)
    val enabledNotificationTypes: Set<NotificationType> = setOf(),

    @ElementCollection
    @CollectionTable(name = "user_notification_channels", joinColumns = [JoinColumn(name = "preference_id")])
    @Enumerated(EnumType.STRING)
    val preferredChannels: Set<NotificationChannel> = setOf(),

    @Column
    val receivePromotional: Boolean = false,

    @Column
    val receiveSystemAlerts: Boolean = true
)
