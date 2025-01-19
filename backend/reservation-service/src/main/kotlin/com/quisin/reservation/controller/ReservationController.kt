package com.quisin.reservation.controller

import com.quisin.reservation.dto.*
import com.quisin.reservation.service.ReservationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "APIs for managing restaurant reservations")
class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Make a reservation for a table")
    fun createReservation(
        @Valid @RequestBody request: CreateReservationRequest
    ): ResponseEntity<ReservationResponse> {
        val createdReservation = reservationService.createReservation(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation)
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "Get reservation by ID", description = "Retrieve details of a specific reservation")
    fun getReservationById(
        @PathVariable reservationId: UUID
    ): ResponseEntity<ReservationResponse> {
        val reservation = reservationService.getReservationById(reservationId)
        return ResponseEntity.ok(reservation)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get reservations for a restaurant", description = "Retrieve all reservations for a specific restaurant")
    fun getReservationsByRestaurant(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<ReservationResponse>> {
        val reservations = reservationService.getReservationsByRestaurant(restaurantId)
        return ResponseEntity.ok(reservations)
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get reservations for a customer", description = "Retrieve all reservations for a specific customer")
    fun getReservationsByCustomer(
        @PathVariable customerId: UUID
    ): ResponseEntity<List<ReservationResponse>> {
        val reservations = reservationService.getReservationsByCustomer(customerId)
        return ResponseEntity.ok(reservations)
    }

    @PutMapping("/{reservationId}")
    @Operation(summary = "Update a reservation", description = "Modify details of an existing reservation")
    fun updateReservation(
        @PathVariable reservationId: UUID,
        @Valid @RequestBody request: UpdateReservationRequest
    ): ResponseEntity<ReservationResponse> {
        val updatedReservation = reservationService.updateReservation(reservationId, request)
        return ResponseEntity.ok(updatedReservation)
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "Cancel a reservation", description = "Cancel an existing reservation")
    fun cancelReservation(
        @PathVariable reservationId: UUID
    ): ResponseEntity<ReservationResponse> {
        val cancelledReservation = reservationService.cancelReservation(reservationId)
        return ResponseEntity.ok(cancelledReservation)
    }

    @PostMapping("/available-tables")
    @Operation(summary = "Find available tables", description = "Search for available tables based on party size and time")
    fun findAvailableTables(
        @Valid @RequestBody request: AvailableTableRequest
    ): ResponseEntity<List<TableResponse>> {
        val availableTables = reservationService.findAvailableTables(request)
        return ResponseEntity.ok(availableTables)
    }
}
