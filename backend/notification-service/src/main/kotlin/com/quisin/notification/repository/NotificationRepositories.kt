package com.quisin.notification.repository

import com.quisin.notification.domain.Notification
import com.quisin.notification.domain.NotificationChannel
import com.quisin.notification.domain.NotificationPreference
import com.quisin.notification.domain.NotificationStatus
import com.quisin.notification.domain.NotificationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByRecipientId(recipientId: UUID): List<Notification>

    fun findByRecipientIdAndStatus(recipientId: UUID, status: NotificationStatus): List<Notification>

    fun findByTypeAndStatus(type: NotificationType, status: NotificationStatus): List<Notification>

    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    fun findByDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Notification>

    fun findByRelatedEntityIdAndRelatedEntityType(
        relatedEntityId: UUID, 
        relatedEntityType: String
    ): List<Notification>
}

@Repository
interface NotificationPreferenceRepository : JpaRepository<NotificationPreference, UUID> {
    fun findByUserId(userId: UUID): NotificationPreference?

    @Query("""
        SELECT np FROM NotificationPreference np 
        WHERE :notificationType MEMBER OF np.enabledNotificationTypes 
        AND :channel MEMBER OF np.preferredChannels
    """)
    fun findByNotificationTypeAndChannel(
        @Param("notificationType") notificationType: NotificationType,
        @Param("channel") channel: NotificationChannel
    ): List<NotificationPreference>
}
