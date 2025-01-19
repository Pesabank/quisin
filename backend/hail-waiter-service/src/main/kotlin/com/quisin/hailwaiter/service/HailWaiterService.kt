package com.quisin.hailwaiter.service

import com.quisin.hailwaiter.domain.WaiterRequest
import com.quisin.hailwaiter.domain.WaiterRequestStatus
import com.quisin.hailwaiter.dto.CreateWaiterRequestRequest
import com.quisin.hailwaiter.dto.UpdateWaiterRequestRequest
import com.quisin.hailwaiter.dto.WaiterRequestResponse
import com.quisin.hailwaiter.repository.WaiterRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class HailWaiterService(
    private val waiterRequestRepository: WaiterRequestRepository,
    private val notificationService: NotificationService
) {
    @Transactional
    fun createWaiterRequest(request: CreateWaiterRequestRequest): WaiterRequestResponse {
        val waiterRequest = WaiterRequest(
            userId = request.userId,
            restaurantId = request.restaurantId,
            tableId = request.tableId,
            reason = request.reason,
            additionalDetails = request.additionalDetails
        )

        val savedRequest = waiterRequestRepository.save(waiterRequest)

        // Notify restaurant staff about the waiter request
        notificationService.notifyStaff(
            restaurantId = savedRequest.restaurantId,
            message = "New waiter request at table ${savedRequest.tableId} - ${savedRequest.reason}"
        )

        return mapToResponse(savedRequest)
    }

    @Transactional
    fun updateWaiterRequest(requestId: UUID, request: UpdateWaiterRequestRequest): WaiterRequestResponse {
        val waiterRequest = waiterRequestRepository.findById(requestId)
            .orElseThrow { WaiterRequestException("Waiter request not found") }

        request.status?.let { waiterRequest.status = it }
        request.assignedWaiterId?.let { waiterRequest.assignedWaiterId = it }

        val updatedRequest = waiterRequestRepository.save(waiterRequest)

        return mapToResponse(updatedRequest)
    }

    fun getWaiterRequestsByTable(restaurantId: UUID, tableId: UUID): List<WaiterRequestResponse> {
        return waiterRequestRepository.findByRestaurantIdAndTableId(restaurantId, tableId)
            .map { mapToResponse(it) }
    }

    fun getActiveWaiterRequests(restaurantId: UUID): List<WaiterRequestResponse> {
        return waiterRequestRepository.findByStatus(WaiterRequestStatus.PENDING)
            .filter { it.restaurantId == restaurantId }
            .map { mapToResponse(it) }
    }

    private fun mapToResponse(waiterRequest: WaiterRequest): WaiterRequestResponse {
        return WaiterRequestResponse(
            id = waiterRequest.id!!,
            userId = waiterRequest.userId,
            restaurantId = waiterRequest.restaurantId,
            tableId = waiterRequest.tableId,
            reason = waiterRequest.reason,
            status = waiterRequest.status,
            additionalDetails = waiterRequest.additionalDetails,
            assignedWaiterId = waiterRequest.assignedWaiterId,
            createdAt = waiterRequest.createdAt!!,
            updatedAt = waiterRequest.updatedAt
        )
    }
}

class WaiterRequestException(message: String) : RuntimeException(message)
