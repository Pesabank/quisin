package com.quisin.menu.controller

import com.quisin.menu.domain.InventoryTransactionType
import com.quisin.menu.dto.InventoryTransactionRequest
import com.quisin.menu.dto.InventoryTransactionResponse
import com.quisin.menu.service.InventoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing inventory transactions")
class InventoryController(
    private val inventoryService: InventoryService
) {
    @PostMapping("/transaction")
    @Operation(summary = "Record inventory transaction", description = "Record a new inventory transaction")
    fun recordInventoryTransaction(
        @Valid @RequestBody request: InventoryTransactionRequest
    ): ResponseEntity<InventoryTransactionResponse> {
        val transaction = inventoryService.recordInventoryTransaction(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get inventory transactions for a restaurant", description = "Retrieve all inventory transactions for a specific restaurant")
    fun getInventoryTransactionsByRestaurant(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<InventoryTransactionResponse>> {
        val transactions = inventoryService.getInventoryTransactionsByRestaurant(restaurantId)
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/menu-item/{menuItemId}")
    @Operation(summary = "Get inventory transactions for a menu item", description = "Retrieve all inventory transactions for a specific menu item")
    fun getInventoryTransactionsByMenuItem(
        @PathVariable menuItemId: UUID
    ): ResponseEntity<List<InventoryTransactionResponse>> {
        val transactions = inventoryService.getInventoryTransactionsByMenuItem(menuItemId)
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/restaurant/{restaurantId}/date-range")
    @Operation(summary = "Get inventory transactions by date range", description = "Retrieve inventory transactions within a specific date range")
    fun getInventoryTransactionsByDateRange(
        @PathVariable restaurantId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<List<InventoryTransactionResponse>> {
        val transactions = inventoryService.getInventoryTransactionsByDateRange(
            restaurantId, startDate, endDate
        )
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/restaurant/{restaurantId}/type")
    @Operation(summary = "Get inventory transactions by type", description = "Retrieve inventory transactions of a specific type")
    fun getInventoryTransactionsByType(
        @PathVariable restaurantId: UUID,
        @RequestParam transactionType: InventoryTransactionType
    ): ResponseEntity<List<InventoryTransactionResponse>> {
        val transactions = inventoryService.getInventoryTransactionsByType(
            restaurantId, transactionType
        )
        return ResponseEntity.ok(transactions)
    }
}
