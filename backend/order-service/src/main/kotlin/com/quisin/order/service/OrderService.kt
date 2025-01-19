package com.quisin.order.service

import com.quisin.order.domain.Order
import com.quisin.order.domain.OrderItem
import com.quisin.order.domain.OrderStatus
import com.quisin.order.dto.*
import com.quisin.order.repository.OrderRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    @Transactional
    fun createOrder(request: CreateOrderRequest): OrderResponse {
        // Validate order total amount
        val totalAmount = calculateTotalAmount(request.items)

        // Create order entity
        val order = Order(
            restaurantId = request.restaurantId,
            customerId = request.customerId,
            waiterId = request.waiterId,
            type = request.type,
            totalAmount = totalAmount,
            items = request.items.map { 
                OrderItem(
                    menuItemId = it.menuItemId,
                    quantity = it.quantity,
                    price = it.price,
                    specialInstructions = it.specialInstructions
                )
            },
            tableNumber = request.tableNumber,
            specialInstructions = request.specialInstructions,
            isWaiterAssignmentRequested = request.requestWaiterAssignment,
            waiterAssignmentStatus = if (request.requestWaiterAssignment) 
                WaiterAssignmentStatus.PENDING 
            else null
        )

        val savedOrder = orderRepository.save(order)

        // Publish order creation event to Kafka
        kafkaTemplate.send("order-created-topic", savedOrder.id.toString(), savedOrder)

        return mapToOrderResponse(savedOrder)
    }

    @Transactional(readOnly = true)
    fun getOrderById(orderId: UUID): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { NoSuchElementException("Order not found") }
        return mapToOrderResponse(order)
    }

    @Transactional(readOnly = true)
    fun getOrdersByRestaurant(restaurantId: UUID): List<OrderSummaryResponse> {
        return orderRepository.findByRestaurantId(restaurantId)
            .map { mapToOrderSummaryResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getOrdersByCustomer(customerId: UUID): List<OrderSummaryResponse> {
        return orderRepository.findByCustomerId(customerId)
            .map { mapToOrderSummaryResponse(it) }
    }

    @Transactional
    fun updateOrderStatus(orderId: UUID, request: UpdateOrderStatusRequest): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { NoSuchElementException("Order not found") }

        val updatedOrder = order.copy(status = request.status)
        val savedOrder = orderRepository.save(updatedOrder)

        // Publish order status update event to Kafka
        kafkaTemplate.send("order-status-updated-topic", savedOrder.id.toString(), savedOrder)

        return mapToOrderResponse(savedOrder)
    }

    @Transactional(readOnly = true)
    fun getOrdersByStatus(status: OrderStatus): List<OrderSummaryResponse> {
        return orderRepository.findByStatus(status)
            .map { mapToOrderSummaryResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getOrdersByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<OrderSummaryResponse> {
        return orderRepository.findOrdersBetweenDates(startDate, endDate)
            .map { mapToOrderSummaryResponse(it) }
    }

    @Transactional
    fun createGroupOrder(request: GroupOrderRequest): GroupOrderResponse {
        // Validate group order request
        validateGroupOrderRequest(request)

        // Generate a unique group order ID
        val groupOrderId = UUID.randomUUID()

        // Calculate total group amount
        val totalGroupAmount = calculateGroupOrderTotalAmount(request)

        // Create individual orders for each participant
        val groupOrders = request.participants.map { participant ->
            createIndividualGroupOrder(
                restaurantId = request.restaurantId,
                customerId = participant.customerId,
                groupOrderId = groupOrderId,
                items = participant.items,
                tableNumber = request.tableNumber,
                specialInstructions = request.specialInstructions,
                isGroupOrderLeader = participant.customerId == request.groupLeaderCustomerId,
                isWaiterAssignmentRequested = request.requestWaiterAssignment,
                waiterAssignmentStatus = if (request.requestWaiterAssignment) 
                    WaiterAssignmentStatus.PENDING 
                else null
            )
        }

        // Return group order response
        return GroupOrderResponse(
            groupOrderId = groupOrderId,
            restaurantId = request.restaurantId,
            groupLeaderCustomerId = request.groupLeaderCustomerId,
            status = OrderStatus.PENDING,
            tableNumber = request.tableNumber,
            specialInstructions = request.specialInstructions,
            orders = groupOrders,
            totalGroupAmount = totalGroupAmount,
            createdAt = LocalDateTime.now(),
            isWaiterAssignmentRequested = request.requestWaiterAssignment,
            waiterAssignmentStatus = if (request.requestWaiterAssignment) 
                WaiterAssignmentStatus.PENDING 
            else null
        )
    }

    private fun createIndividualGroupOrder(
        restaurantId: UUID,
        customerId: UUID,
        groupOrderId: UUID,
        items: List<OrderItemRequest>,
        tableNumber: String?,
        specialInstructions: String?,
        isGroupOrderLeader: Boolean,
        isWaiterAssignmentRequested: Boolean,
        waiterAssignmentStatus: WaiterAssignmentStatus?
    ): OrderResponse {
        // Calculate total amount for this participant's order
        val totalAmount = calculateTotalAmount(items)

        // Create order entity
        val order = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            type = OrderType.GROUP,
            totalAmount = totalAmount,
            items = items.map { 
                OrderItem(
                    menuItemId = it.menuItemId,
                    quantity = it.quantity,
                    price = it.price,
                    specialInstructions = it.specialInstructions
                )
            },
            tableNumber = tableNumber,
            specialInstructions = specialInstructions,
            groupOrderId = groupOrderId,
            isGroupOrderLeader = isGroupOrderLeader,
            isWaiterAssignmentRequested = isWaiterAssignmentRequested,
            waiterAssignmentStatus = waiterAssignmentStatus
        )

        val savedOrder = orderRepository.save(order)

        // Publish group order creation event
        kafkaTemplate.send("group-order-created-topic", groupOrderId.toString(), savedOrder)

        return mapToOrderResponse(savedOrder)
    }

    @Transactional(readOnly = true)
    fun getGroupOrder(groupOrderId: UUID): GroupOrderResponse {
        // Find the group order leader
        val groupOrderLeader = orderRepository.findByGroupOrderIdAndIsGroupOrderLeaderTrue(groupOrderId)
            ?: throw NoSuchElementException("Group order not found")

        // Find all orders in the group
        val groupOrders = orderRepository.findByGroupOrderId(groupOrderId)

        // Map to response
        return GroupOrderResponse(
            groupOrderId = groupOrderId,
            restaurantId = groupOrderLeader.restaurantId,
            groupLeaderCustomerId = groupOrderLeader.customerId,
            status = groupOrderLeader.status,
            tableNumber = groupOrderLeader.tableNumber,
            specialInstructions = groupOrderLeader.specialInstructions,
            orders = groupOrders.map { mapToOrderResponse(it) },
            totalGroupAmount = groupOrders.fold(BigDecimal.ZERO) { acc, order -> acc.plus(order.totalAmount) },
            createdAt = groupOrderLeader.createdAt,
            isWaiterAssignmentRequested = groupOrderLeader.isWaiterAssignmentRequested,
            waiterAssignmentStatus = groupOrderLeader.waiterAssignmentStatus
        )
    }

    @Transactional
    fun updateGroupOrderStatus(groupOrderId: UUID, status: OrderStatus): GroupOrderResponse {
        // Find and update all orders in the group
        val groupOrders = orderRepository.findByGroupOrderId(groupOrderId)
        
        val updatedOrders = groupOrders.map { order ->
            val updatedOrder = order.copy(status = status)
            orderRepository.save(updatedOrder)
        }

        // Publish group order status update event
        kafkaTemplate.send("group-order-status-updated-topic", groupOrderId.toString(), status)

        // Return updated group order response
        return GroupOrderResponse(
            groupOrderId = groupOrderId,
            restaurantId = updatedOrders.first().restaurantId,
            groupLeaderCustomerId = updatedOrders.first { it.isGroupOrderLeader }.customerId,
            status = status,
            tableNumber = updatedOrders.first().tableNumber,
            specialInstructions = updatedOrders.first().specialInstructions,
            orders = updatedOrders.map { mapToOrderResponse(it) },
            totalGroupAmount = updatedOrders.fold(BigDecimal.ZERO) { acc, order -> acc.plus(order.totalAmount) },
            createdAt = updatedOrders.first().createdAt,
            isWaiterAssignmentRequested = updatedOrders.first().isWaiterAssignmentRequested,
            waiterAssignmentStatus = updatedOrders.first().waiterAssignmentStatus
        )
    }

    @Transactional
    fun assignWaiterToOrder(request: WaiterAssignmentRequest): OrderResponse {
        val order = orderRepository.findById(request.orderId)
            .orElseThrow { NoSuchElementException("Order not found") }

        // Update order with waiter assignment details
        val updatedOrder = order.copy(
            assignedWaiterId = request.waiterId,
            waiterAssignmentStatus = request.status,
            isWaiterAssignmentRequested = true
        )

        val savedOrder = orderRepository.save(updatedOrder)

        // Publish waiter assignment event
        kafkaTemplate.send("waiter-assigned-topic", savedOrder.id.toString(), savedOrder)

        return mapToOrderResponse(savedOrder)
    }

    @Transactional
    fun assignWaiterToGroupOrder(groupOrderId: UUID, waiterId: UUID): GroupOrderResponse {
        // Find all orders in the group
        val groupOrders = orderRepository.findByGroupOrderId(groupOrderId)
        
        // Update each order with waiter assignment
        val updatedOrders = groupOrders.map { order ->
            val updatedOrder = order.copy(
                assignedWaiterId = waiterId,
                waiterAssignmentStatus = WaiterAssignmentStatus.ASSIGNED,
                isWaiterAssignmentRequested = true
            )
            orderRepository.save(updatedOrder)
        }

        // Publish group waiter assignment event
        kafkaTemplate.send("group-waiter-assigned-topic", groupOrderId.toString(), waiterId)

        // Return updated group order response
        return GroupOrderResponse(
            groupOrderId = groupOrderId,
            restaurantId = updatedOrders.first().restaurantId,
            groupLeaderCustomerId = updatedOrders.first { it.isGroupOrderLeader }.customerId,
            status = updatedOrders.first().status,
            tableNumber = updatedOrders.first().tableNumber,
            specialInstructions = updatedOrders.first().specialInstructions,
            orders = updatedOrders.map { mapToOrderResponse(it) },
            totalGroupAmount = updatedOrders.fold(BigDecimal.ZERO) { acc, order -> acc.plus(order.totalAmount) },
            createdAt = updatedOrders.first().createdAt,
            isWaiterAssignmentRequested = true,
            waiterAssignmentStatus = WaiterAssignmentStatus.ASSIGNED
        )
    }

    private fun validateGroupOrderRequest(request: GroupOrderRequest) {
        // Ensure group leader is among participants
        require(request.participants.any { it.customerId == request.groupLeaderCustomerId }) {
            "Group leader must be among the participants"
        }

        // Ensure unique customers in the group
        require(request.participants.map { it.customerId }.distinct().size == request.participants.size) {
            "Duplicate customers in group order"
        }
    }

    private fun calculateGroupOrderTotalAmount(request: GroupOrderRequest): BigDecimal {
        return request.participants.fold(BigDecimal.ZERO) { acc, participant ->
            acc.plus(calculateTotalAmount(participant.items))
        }
    }

    private fun calculateTotalAmount(items: List<OrderItemRequest>): BigDecimal {
        return items.fold(BigDecimal.ZERO) { acc, item -> 
            acc.plus(item.price.multiply(BigDecimal(item.quantity))) 
        }
    }

    private fun mapToOrderResponse(order: Order): OrderResponse {
        return OrderResponse(
            id = order.id!!,
            restaurantId = order.restaurantId,
            customerId = order.customerId,
            waiterId = order.waiterId,
            status = order.status,
            type = order.type,
            totalAmount = order.totalAmount,
            items = order.items.map { 
                OrderItemResponse(
                    menuItemId = it.menuItemId,
                    quantity = it.quantity,
                    price = it.price,
                    specialInstructions = it.specialInstructions
                )
            },
            tableNumber = order.tableNumber,
            specialInstructions = order.specialInstructions,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
            assignedWaiterId = order.assignedWaiterId,
            isWaiterAssignmentRequested = order.isWaiterAssignmentRequested,
            waiterAssignmentStatus = order.waiterAssignmentStatus
        )
    }

    private fun mapToOrderSummaryResponse(order: Order): OrderSummaryResponse {
        return OrderSummaryResponse(
            id = order.id!!,
            status = order.status,
            type = order.type,
            totalAmount = order.totalAmount,
            createdAt = order.createdAt
        )
    }
}
