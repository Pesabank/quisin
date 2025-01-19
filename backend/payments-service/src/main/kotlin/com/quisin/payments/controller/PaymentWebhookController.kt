package com.quisin.payments.controller

import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.dto.UpdatePaymentRequest
import com.quisin.payments.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/payments/webhooks")
class PaymentWebhookController(
    private val paymentService: PaymentService
) {
    @PostMapping("/mpesa")
    fun handleMpesaWebhook(
        @RequestBody payload: MpesaWebhookPayload,
        @RequestHeader("X-Mpesa-Signature") signature: String
    ): ResponseEntity<Any> {
        // Verify webhook signature
        // Update payment status based on M-PESA callback
        val paymentId = UUID.fromString(payload.merchantRequestId)
        val status = when (payload.resultCode) {
            "0" -> PaymentStatus.SUCCESSFUL
            else -> PaymentStatus.FAILED
        }
        
        paymentService.updatePaymentStatus(
            paymentId,
            UpdatePaymentRequest(
                status = status,
                externalTransactionId = payload.mpesaReceiptNumber
            )
        )
        
        return ResponseEntity.ok().build()
    }

    @PostMapping("/stripe")
    fun handleStripeWebhook(
        @RequestBody payload: String,
        @RequestHeader("Stripe-Signature") signature: String
    ): ResponseEntity<Any> {
        // Verify Stripe webhook signature
        // Handle different event types (payment_intent.succeeded, payment_intent.failed, etc.)
        // Update payment status accordingly
        return ResponseEntity.ok().build()
    }

    @PostMapping("/paypal")
    fun handlePayPalWebhook(
        @RequestBody payload: PayPalWebhookPayload,
        @RequestHeader("PayPal-Transmission-Id") transmissionId: String,
        @RequestHeader("PayPal-Transmission-Sig") signature: String
    ): ResponseEntity<Any> {
        // Verify PayPal webhook signature
        // Handle different event types (PAYMENT.CAPTURE.COMPLETED, PAYMENT.CAPTURE.DENIED, etc.)
        // Update payment status accordingly
        return ResponseEntity.ok().build()
    }

    @PostMapping("/coinbase")
    fun handleCoinbaseWebhook(
        @RequestBody payload: CoinbaseWebhookPayload,
        @RequestHeader("X-CC-Webhook-Signature") signature: String
    ): ResponseEntity<Any> {
        // Verify Coinbase webhook signature
        // Handle different event types (charge:confirmed, charge:failed, etc.)
        // Update payment status accordingly
        return ResponseEntity.ok().build()
    }
}

// Data classes for webhook payloads
data class MpesaWebhookPayload(
    val merchantRequestId: String,
    val checkoutRequestId: String,
    val resultCode: String,
    val resultDesc: String,
    val mpesaReceiptNumber: String,
    val transactionDate: String,
    val phoneNumber: String,
    val amount: String
)

data class PayPalWebhookPayload(
    val eventType: String,
    val resourceType: String,
    val resource: Map<String, Any>,
    val eventVersion: String,
    val summary: String
)

data class CoinbaseWebhookPayload(
    val eventType: String,
    val data: Map<String, Any>
) 