package com.quisin.reservation.service

import com.quisin.reservation.domain.RestaurantTable
import com.quisin.reservation.domain.TableStatus
import com.quisin.reservation.domain.TableType
import com.quisin.reservation.dto.CreateTableRequest
import com.quisin.reservation.dto.TableResponse
import com.quisin.reservation.repository.TableRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TableService(
    private val tableRepository: TableRepository
) {
    @Transactional
    fun createTable(request: CreateTableRequest): TableResponse {
        val table = RestaurantTable(
            restaurantId = request.restaurantId,
            tableNumber = request.tableNumber,
            type = request.type,
            capacity = request.capacity,
            location = request.location,
            description = request.description,
            minimumSpend = request.minimumSpend
        )

        val savedTable = tableRepository.save(table)
        return mapToTableResponse(savedTable)
    }

    @Transactional(readOnly = true)
    fun getTableById(tableId: UUID): TableResponse {
        val table = tableRepository.findById(tableId)
            .orElseThrow { NoSuchElementException("Table not found") }
        return mapToTableResponse(table)
    }

    @Transactional(readOnly = true)
    fun getTablesByRestaurant(restaurantId: UUID): List<TableResponse> {
        return tableRepository.findByRestaurantId(restaurantId)
            .map { mapToTableResponse(it) }
    }

    @Transactional
    fun updateTableStatus(tableId: UUID, status: TableStatus): TableResponse {
        val table = tableRepository.findById(tableId)
            .orElseThrow { NoSuchElementException("Table not found") }

        val updatedTable = table.copy(status = status)
        val savedTable = tableRepository.save(updatedTable)
        return mapToTableResponse(savedTable)
    }

    @Transactional(readOnly = true)
    fun findAvailableTables(
        restaurantId: UUID, 
        partySize: Int, 
        startTime: java.time.LocalDateTime, 
        endTime: java.time.LocalDateTime
    ): List<TableResponse> {
        return tableRepository.findAvailableTables(restaurantId, partySize, startTime, endTime)
            .map { mapToTableResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getTablesByType(restaurantId: UUID, type: TableType): List<TableResponse> {
        return tableRepository.findByRestaurantIdAndType(restaurantId, type)
            .map { mapToTableResponse(it) }
    }

    @Transactional
    fun deleteTable(tableId: UUID) {
        val table = tableRepository.findById(tableId)
            .orElseThrow { NoSuchElementException("Table not found") }
        
        tableRepository.delete(table)
    }

    private fun mapToTableResponse(table: RestaurantTable): TableResponse {
        return TableResponse(
            id = table.id!!,
            restaurantId = table.restaurantId,
            tableNumber = table.tableNumber,
            status = table.status,
            type = table.type,
            capacity = table.capacity,
            location = table.location,
            description = table.description,
            minimumSpend = table.minimumSpend,
            createdAt = table.createdAt
        )
    }
}
