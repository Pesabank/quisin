package com.quisin.reservation.service

import com.quisin.reservation.domain.Reservation
import com.quisin.reservation.domain.ReservationStatus
import com.quisin.reservation.domain.TableStatus
import com.quisin.reservation.dto.AvailableTableRequest
import com.quisin.reservation.dto.CreateReservationRequest
import com.quisin.reservation.dto.ReservationResponse
import com.quisin.reservation.dto.TableResponse
import com.quisin.reservation.dto.UpdateReservationRequest
import com.quisin.reservation.repository.ReservationRepository
import com.quisin.reservation.repository.TableRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val tableRepository: TableRepository,
    private val tableService: TableService
) {
    @Transactional
    fun createReservation(request: CreateReservationRequest): ReservationResponse {
        // Validate table availability
        val availableTables = tableService.findAvailableTables(
            request.restaurantId, 
            request.partySize, 
            request.reservationDateTime,
            request.reservationDateTime.plusHours(request.estimatedDuration?.toLong() ?: 2)
        )

        // Check if the requested table is available
        val requestedTable = availableTables.find { it.id == request.tableId }
            ?: throw IllegalArgumentException("Selected table is not available")

        // Create reservation
        val reservation = Reservation(
            restaurantId = request.restaurantId,
            customerId = request.customerId,
            tableId = request.tableId,
            reservationDateTime = request.reservationDateTime,
            partySize = request.partySize,
            contactName = request.contactName,
            contactPhone = request.contactPhone,
            contactEmail = request.contactEmail,
            specialRequests = request.specialRequests,
            estimatedDuration = request.estimatedDuration
        )

        val savedReservation = reservationRepository.save(reservation)

        // Update table status
        tableService.updateTableStatus(request.tableId, TableStatus.RESERVED)

        return mapToReservationResponse(savedReservation)
    }

    @Transactional(readOnly = true)
    fun getReservationById(reservationId: UUID): ReservationResponse {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found") }
        return mapToReservationResponse(reservation)
    }

    @Transactional(readOnly = true)
    fun getReservationsByRestaurant(restaurantId: UUID): List<ReservationResponse> {
        return reservationRepository.findByRestaurantId(restaurantId)
            .map { mapToReservationResponse(it) }
    }

    @Transactional
    fun updateReservation(reservationId: UUID, request: UpdateReservationRequest): ReservationResponse {
        val existingReservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found") }

        val updatedReservation = existingReservation.copy(
            reservationDateTime = request.reservationDateTime ?: existingReservation.reservationDateTime,
            partySize = request.partySize ?: existingReservation.partySize,
            status = request.status ?: existingReservation.status,
            specialRequests = request.specialRequests ?: existingReservation.specialRequests,
            contactName = request.contactName ?: existingReservation.contactName,
            contactPhone = request.contactPhone ?: existingReservation.contactPhone,
            contactEmail = request.contactEmail ?: existingReservation.contactEmail,
            estimatedDuration = request.estimatedDuration ?: existingReservation.estimatedDuration
        )

        val savedReservation = reservationRepository.save(updatedReservation)
        return mapToReservationResponse(savedReservation)
    }

    @Transactional
    fun cancelReservation(reservationId: UUID): ReservationResponse {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found") }

        val cancelledReservation = reservation.copy(status = ReservationStatus.CANCELLED)
        val savedReservation = reservationRepository.save(cancelledReservation)

        // Update table status back to available
        tableService.updateTableStatus(reservation.tableId, TableStatus.AVAILABLE)

        return mapToReservationResponse(savedReservation)
    }

    @Transactional(readOnly = true)
    fun findAvailableTables(request: AvailableTableRequest): List<TableResponse> {
        // Default estimated duration to 2 hours if not specified
        val endTime = request.reservationDateTime.plusHours(2)

        return tableService.findAvailableTables(
            request.restaurantId, 
            request.partySize, 
            request.reservationDateTime, 
            endTime
        )
    }

    @Transactional(readOnly = true)
    fun getReservationsByCustomer(customerId: UUID): List<ReservationResponse> {
        return reservationRepository.findByCustomerId(customerId)
            .map { mapToReservationResponse(it) }
    }

    private fun mapToReservationResponse(reservation: Reservation): ReservationResponse {
        return ReservationResponse(
            id = reservation.id!!,
            restaurantId = reservation.restaurantId,
            customerId = reservation.customerId,
            tableId = reservation.tableId,
            reservationDateTime = reservation.reservationDateTime,
            partySize = reservation.partySize,
            status = reservation.status,
            contactName = reservation.contactName,
            contactPhone = reservation.contactPhone,
            contactEmail = reservation.contactEmail,
            specialRequests = reservation.specialRequests,
            estimatedDuration = reservation.estimatedDuration,
            createdAt = reservation.createdAt
        )
    }
}
