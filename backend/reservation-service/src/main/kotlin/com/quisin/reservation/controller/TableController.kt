package com.quisin.reservation.controller

import com.quisin.reservation.domain.TableStatus
import com.quisin.reservation.domain.TableType
import com.quisin.reservation.dto.CreateTableRequest
import com.quisin.reservation.dto.TableResponse
import com.quisin.reservation.service.TableService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/tables")
@Tag(name = "Table Management", description = "APIs for managing restaurant tables")
class TableController(
    private val tableService: TableService
) {
    @PostMapping
    @Operation(summary = "Create a new table", description = "Add a new table to the restaurant")
    fun createTable(
        @Valid @RequestBody request: CreateTableRequest
    ): ResponseEntity<TableResponse> {
        val createdTable = tableService.createTable(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTable)
    }

    @GetMapping("/{tableId}")
    @Operation(summary = "Get table by ID", description = "Retrieve details of a specific table")
    fun getTableById(
        @PathVariable tableId: UUID
    ): ResponseEntity<TableResponse> {
        val table = tableService.getTableById(tableId)
        return ResponseEntity.ok(table)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get tables for a restaurant", description = "Retrieve all tables for a specific restaurant")
    fun getTablesByRestaurant(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<TableResponse>> {
        val tables = tableService.getTablesByRestaurant(restaurantId)
        return ResponseEntity.ok(tables)
    }

    @PutMapping("/{tableId}/status")
    @Operation(summary = "Update table status", description = "Change the status of a specific table")
    fun updateTableStatus(
        @PathVariable tableId: UUID,
        @RequestParam status: TableStatus
    ): ResponseEntity<TableResponse> {
        val updatedTable = tableService.updateTableStatus(tableId, status)
        return ResponseEntity.ok(updatedTable)
    }

    @GetMapping("/restaurant/{restaurantId}/type")
    @Operation(summary = "Get tables by type", description = "Retrieve tables of a specific type for a restaurant")
    fun getTablesByType(
        @PathVariable restaurantId: UUID,
        @RequestParam type: TableType
    ): ResponseEntity<List<TableResponse>> {
        val tables = tableService.getTablesByType(restaurantId, type)
        return ResponseEntity.ok(tables)
    }

    @DeleteMapping("/{tableId}")
    @Operation(summary = "Delete a table", description = "Remove a table from the restaurant")
    fun deleteTable(
        @PathVariable tableId: UUID
    ): ResponseEntity<Void> {
        tableService.deleteTable(tableId)
        return ResponseEntity.noContent().build()
    }
}
