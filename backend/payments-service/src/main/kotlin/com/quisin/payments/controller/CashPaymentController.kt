package com.quisin.payments.controller

import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.dto.UpdatePaymentRequest
import com.quisin.payments.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/payments/cash")
class CashPaymentController(
    private val paymentService: PaymentService
) {
    @PatchMapping("/{paymentId}/confirm")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    fun confirmCashPayment(
        @PathVariable paymentId: UUID,
        @RequestParam amountReceived: String,
        @RequestParam staffId: UUID
    ): ResponseEntity<Any> {
        paymentService.updatePaymentStatus(
            paymentId,
            UpdatePaymentRequest(
                status = PaymentStatus.SUCCESSFUL,
                externalTransactionId = "CASH-${UUID.randomUUID()}",
                metadata = mapOf(
                    "amountReceived" to amountReceived,
                    "staffId" to staffId.toString(),
                    "confirmedAt" to System.currentTimeMillis().toString()
                )
            )
        )
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/{paymentId}/cancel")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    fun cancelCashPayment(
        @PathVariable paymentId: UUID,
        @RequestParam reason: String,
        @RequestParam staffId: UUID
    ): ResponseEntity<Any> {
        paymentService.updatePaymentStatus(
            paymentId,
            UpdatePaymentRequest(
                status = PaymentStatus.CANCELLED,
                metadata = mapOf(
                    "reason" to reason,
                    "staffId" to staffId.toString(),
                    "cancelledAt" to System.currentTimeMillis().toString()
                )
            )
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    fun getPendingCashPayments(
        @RequestParam restaurantId: UUID
    ): ResponseEntity<Any> {
        val pendingPayments = paymentService.getPendingCashPayments(restaurantId)
        return ResponseEntity.ok(pendingPayments)
    }
} 