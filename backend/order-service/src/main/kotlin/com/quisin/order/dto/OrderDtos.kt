package com.quisin.order.dto

import com.quisin.order.domain.OrderStatus
import com.quisin.order.domain.OrderType
import com.quisin.order.domain.WaiterAssignmentStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class OrderItemRequest(
    @field:NotNull(message = "Menu item ID is required")
    val menuItemId: UUID,

    @field:Positive(message = "Quantity must be positive")
    val quantity: Int,

    @field:PositiveOrZero(message = "Price must be non-negative")
    val price: BigDecimal,

    val specialInstructions: String? = null
)

data class CreateOrderRequest(
    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:NotNull(message = "Customer ID is required")
    val customerId: UUID,

    val waiterId: UUID? = null,

    @field:NotNull(message = "Order type is required")
    val type: OrderType,

    @field:Valid
    @field:NotEmpty(message = "Order must contain at least one item")
    val items: List<OrderItemRequest>,

    val tableNumber: String? = null,
    val specialInstructions: String? = null,

    val groupOrderId: UUID? = null,
    val isGroupOrderLeader: Boolean = false,

    val requestWaiterAssignment: Boolean = false
)

data class GroupOrderRequest(
    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:NotNull(message = "Group leader customer ID is required")
    val groupLeaderCustomerId: UUID,

    @field:NotEmpty(message = "Group must have at least one participant")
    val participants: List<GroupOrderParticipant>,

    val tableNumber: String? = null,
    val specialInstructions: String? = null,

    val requestWaiterAssignment: Boolean = false
)

data class GroupOrderParticipant(
    @field:NotNull(message = "Customer ID is required")
    val customerId: UUID,

    @field:Valid
    @field:NotEmpty(message = "Participant must have at least one item")
    val items: List<OrderItemRequest>
)

data class UpdateOrderStatusRequest(
    @field:NotNull(message = "Order status is required")
    val status: OrderStatus
)

data class WaiterAssignmentRequest(
    @field:NotNull(message = "Waiter ID is required")
    val waiterId: UUID,

    @field:NotNull(message = "Order ID is required")
    val orderId: UUID,

    val status: WaiterAssignmentStatus = WaiterAssignmentStatus.ASSIGNED
)

data class OrderResponse(
    val id: UUID,
    val restaurantId: UUID,
    val customerId: UUID,
    val waiterId: UUID?,
    val status: OrderStatus,
    val type: OrderType,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponse>,
    val tableNumber: String?,
    val specialInstructions: String?,
    val groupOrderId: UUID?,
    val isGroupOrderLeader: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,

    val assignedWaiterId: UUID? = null,
    val isWaiterAssignmentRequested: Boolean,
    val waiterAssignmentStatus: WaiterAssignmentStatus?
)

data class OrderItemResponse(
    val menuItemId: UUID,
    val quantity: Int,
    val price: BigDecimal,
    val specialInstructions: String?
)

data class OrderSummaryResponse(
    val id: UUID,
    val status: OrderStatus,
    val type: OrderType,
    val totalAmount: BigDecimal,
    val createdAt: LocalDateTime?
)

data class GroupOrderResponse(
    val groupOrderId: UUID,
    val restaurantId: UUID,
    val groupLeaderCustomerId: UUID,
    val status: OrderStatus,
    val tableNumber: String?,
    val specialInstructions: String?,
    val orders: List<OrderResponse>,
    val totalGroupAmount: BigDecimal,
    val createdAt: LocalDateTime?,

    val isWaiterAssignmentRequested: Boolean,
    val waiterAssignmentStatus: WaiterAssignmentStatus?
)
