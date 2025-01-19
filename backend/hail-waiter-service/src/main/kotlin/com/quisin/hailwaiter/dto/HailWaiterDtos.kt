package com.quisin.hailwaiter.dto

import com.quisin.hailwaiter.domain.WaiterRequestReason
import com.quisin.hailwaiter.domain.WaiterRequestStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.UUID

data class CreateWaiterRequestRequest(
    @field:NotNull(message = "User ID is required")
    val userId: UUID,

    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:NotNull(message = "Table ID is required")
    val tableId: UUID,

    @field:NotNull(message = "Request reason is required")
    val reason: WaiterRequestReason,

    val additionalDetails: String? = null
)

data class UpdateWaiterRequestRequest(
    val status: WaiterRequestStatus? = null,
    val assignedWaiterId: UUID? = null
)

data class WaiterRequestResponse(
    val id: UUID,
    val userId: UUID,
    val restaurantId: UUID,
    val tableId: UUID,
    val reason: WaiterRequestReason,
    val status: WaiterRequestStatus,
    val additionalDetails: String?,
    val assignedWaiterId: UUID?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
