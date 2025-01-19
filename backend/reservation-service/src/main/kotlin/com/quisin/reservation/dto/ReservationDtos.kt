package com.quisin.reservation.dto

import com.quisin.reservation.domain.ReservationStatus
import com.quisin.reservation.domain.TableStatus
import com.quisin.reservation.domain.TableType
import jakarta.validation.constraints.*
import java.time.LocalDateTime
import java.util.UUID

data class CreateTableRequest(
    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:NotBlank(message = "Table number is required")
    val tableNumber: String,

    @field:NotNull(message = "Table type is required")
    val type: TableType,

    @field:Positive(message = "Table capacity must be a positive number")
    val capacity: Int,

    val location: String? = null,
    val description: String? = null,
    val minimumSpend: Double? = null
)

data class TableResponse(
    val id: UUID,
    val restaurantId: UUID,
    val tableNumber: String,
    val status: TableStatus,
    val type: TableType,
    val capacity: Int,
    val location: String? = null,
    val description: String? = null,
    val minimumSpend: Double? = null,
    val createdAt: LocalDateTime? = null
)

data class CreateReservationRequest(
    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:NotNull(message = "Customer ID is required")
    val customerId: UUID,

    @field:NotNull(message = "Table ID is required")
    val tableId: UUID,

    @field:Future(message = "Reservation date must be in the future")
    val reservationDateTime: LocalDateTime,

    @field:Positive(message = "Party size must be a positive number")
    @field:Max(value = 20, message = "Maximum party size is 20")
    val partySize: Int,

    @field:NotBlank(message = "Contact name is required")
    val contactName: String,

    @field:NotBlank(message = "Contact phone is required")
    val contactPhone: String,

    val contactEmail: String? = null,
    val specialRequests: String? = null,
    val estimatedDuration: Int? = null
)

data class ReservationResponse(
    val id: UUID,
    val restaurantId: UUID,
    val customerId: UUID,
    val tableId: UUID,
    val reservationDateTime: LocalDateTime,
    val partySize: Int,
    val status: ReservationStatus,
    val contactName: String,
    val contactPhone: String,
    val contactEmail: String? = null,
    val specialRequests: String? = null,
    val estimatedDuration: Int? = null,
    val createdAt: LocalDateTime? = null
)

data class UpdateReservationRequest(
    val reservationDateTime: LocalDateTime? = null,
    val partySize: Int? = null,
    val status: ReservationStatus? = null,
    val specialRequests: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val estimatedDuration: Int? = null
)

data class AvailableTableRequest(
    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:Future(message = "Reservation date must be in the future")
    val reservationDateTime: LocalDateTime,

    @field:Positive(message = "Party size must be a positive number")
    @field:Max(value = 20, message = "Maximum party size is 20")
    val partySize: Int
)
