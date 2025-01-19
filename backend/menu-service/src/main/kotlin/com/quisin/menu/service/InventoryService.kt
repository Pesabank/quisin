package com.quisin.menu.service

import com.quisin.menu.domain.InventoryTransaction
import com.quisin.menu.domain.InventoryTransactionType
import com.quisin.menu.domain.MenuItem
import com.quisin.menu.domain.MenuItemStatus
import com.quisin.menu.dto.InventoryTransactionRequest
import com.quisin.menu.dto.InventoryTransactionResponse
import com.quisin.menu.repository.InventoryTransactionRepository
import com.quisin.menu.repository.MenuItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class InventoryService(
    private val inventoryTransactionRepository: InventoryTransactionRepository,
    private val menuItemRepository: MenuItemRepository
) {
    @Transactional
    fun recordInventoryTransaction(request: InventoryTransactionRequest): InventoryTransactionResponse {
        // Validate menu item exists
        val menuItem = menuItemRepository.findById(request.menuItemId)
            .orElseThrow { NoSuchElementException("Menu item not found") }

        // Create inventory transaction
        val transaction = InventoryTransaction(
            restaurantId = request.restaurantId,
            menuItemId = request.menuItemId,
            type = request.type,
            quantity = request.quantity,
            notes = request.notes,
            employeeId = request.employeeId
        )

        val savedTransaction = inventoryTransactionRepository.save(transaction)

        // Update menu item stock based on transaction type
        updateMenuItemStock(menuItem, request.type, request.quantity)

        return mapToInventoryTransactionResponse(savedTransaction)
    }

    @Transactional(readOnly = true)
    fun getInventoryTransactionsByRestaurant(restaurantId: UUID): List<InventoryTransactionResponse> {
        return inventoryTransactionRepository.findByRestaurantId(restaurantId)
            .map { mapToInventoryTransactionResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getInventoryTransactionsByMenuItem(menuItemId: UUID): List<InventoryTransactionResponse> {
        return inventoryTransactionRepository.findByMenuItemId(menuItemId)
            .map { mapToInventoryTransactionResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getInventoryTransactionsByDateRange(
        restaurantId: UUID, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<InventoryTransactionResponse> {
        return inventoryTransactionRepository.findByRestaurantIdAndCreatedAtBetween(
            restaurantId, startDate, endDate
        ).map { mapToInventoryTransactionResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getInventoryTransactionsByType(
        restaurantId: UUID, 
        transactionType: InventoryTransactionType
    ): List<InventoryTransactionResponse> {
        return inventoryTransactionRepository.findByRestaurantIdAndTransactionType(
            restaurantId, transactionType
        ).map { mapToInventoryTransactionResponse(it) }
    }

    private fun updateMenuItemStock(
        menuItem: MenuItem, 
        transactionType: InventoryTransactionType, 
        quantity: Int
    ) {
        val updatedMenuItem = when (transactionType) {
            InventoryTransactionType.STOCK_IN -> 
                menuItem.copy(currentStock = menuItem.currentStock + quantity)
            
            InventoryTransactionType.STOCK_OUT, 
            InventoryTransactionType.SPOILAGE -> 
                menuItem.copy(
                    currentStock = (menuItem.currentStock - quantity).coerceAtLeast(0),
                    status = if (menuItem.currentStock - quantity <= 0) 
                        MenuItemStatus.OUT_OF_STOCK 
                    else menuItem.status
                )
            
            InventoryTransactionType.ADJUSTMENT -> 
                menuItem.copy(currentStock = quantity)
        }

        menuItemRepository.save(updatedMenuItem)
    }

    private fun mapToInventoryTransactionResponse(
        transaction: InventoryTransaction
    ): InventoryTransactionResponse {
        return InventoryTransactionResponse(
            id = transaction.id!!,
            restaurantId = transaction.restaurantId,
            menuItemId = transaction.menuItemId,
            type = transaction.type,
            quantity = transaction.quantity,
            notes = transaction.notes,
            employeeId = transaction.employeeId,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
    }
}
