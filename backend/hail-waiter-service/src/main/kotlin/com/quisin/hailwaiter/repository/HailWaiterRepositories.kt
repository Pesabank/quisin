package com.quisin.hailwaiter.repository

import com.quisin.hailwaiter.domain.WaiterRequest
import com.quisin.hailwaiter.domain.WaiterRequestReason
import com.quisin.hailwaiter.domain.WaiterRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface WaiterRequestRepository : JpaRepository<WaiterRequest, UUID> {
    fun findByRestaurantIdAndTableId(restaurantId: UUID, tableId: UUID): List<WaiterRequest>
    
    fun findByUserIdAndRestaurantId(userId: UUID, restaurantId: UUID): List<WaiterRequest>
    
    fun findByStatus(status: WaiterRequestStatus): List<WaiterRequest>
    
    fun findByReasonAndStatus(reason: WaiterRequestReason, status: WaiterRequestStatus): List<WaiterRequest>
}
