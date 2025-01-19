package com.quisin.payments.dto

import com.quisin.payments.domain.PaymentMethod
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.domain.PaymentType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreatePaymentRequest(
    @field:NotNull(message = "User ID is required")
    val userId: UUID,

    @field:NotNull(message = "Order ID is required")
    val orderId: UUID,

    @field:NotNull(message = "Payment method is required")
    val paymentMethod: PaymentMethod,

    @field:NotNull(message = "Payment type is required")
    val paymentType: PaymentType,

    @field:DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    val amount: BigDecimal,

    val currency: String = "USD",
    val description: String? = null
)

data class CreateSplitPaymentRequest(
    @field:NotNull(message = "Order ID is required")
    val orderId: UUID,

    @field:NotNull(message = "Payment method is required")
    val paymentMethod: PaymentMethod,

    val participants: List<ParticipantPayment>
)

data class ParticipantPayment(
    @field:NotNull(message = "User ID is required")
    val userId: UUID,

    @field:DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    val amount: BigDecimal
)

data class UpdatePaymentRequest(
    val status: PaymentStatus? = null,
    val externalTransactionId: String? = null
)

data class PaymentResponse(
    val id: UUID,
    val userId: UUID,
    val orderId: UUID,
    val paymentMethod: PaymentMethod,
    val paymentType: PaymentType,
    val status: PaymentStatus,
    val amount: BigDecimal,
    val currency: String,
    val description: String?,
    val externalTransactionId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

data class PaymentSplitResponse(
    val id: UUID,
    val paymentId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val status: PaymentStatus
)
