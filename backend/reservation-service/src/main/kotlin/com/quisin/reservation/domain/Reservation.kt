package com.quisin.reservation.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class ReservationStatus {
    CONFIRMED,   // Reservation is active and confirmed
    PENDING,     // Reservation is awaiting confirmation
    CANCELLED,   // Reservation has been cancelled
    COMPLETED,   // Reservation has been fulfilled
    NO_SHOW      // Customer did not arrive
}

@Entity
@Table(name = "reservations")
@EntityListeners(AuditingEntityListener::class)
data class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val customerId: UUID,

    @Column(nullable = false)
    val tableId: UUID,

    @Column(nullable = false)
    val reservationDateTime: LocalDateTime,

    @Column(nullable = false)
    val partySize: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus = ReservationStatus.PENDING,

    @Column
    val specialRequests: String? = null,

    @Column
    val contactName: String,

    @Column
    val contactPhone: String,

    @Column
    val contactEmail: String? = null,

    @Column
    val estimatedDuration: Int? = null,  // in minutes

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null
)
