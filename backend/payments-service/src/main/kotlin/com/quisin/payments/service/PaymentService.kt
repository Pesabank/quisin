package com.quisin.payments.service

import com.quisin.payments.domain.Payment
import com.quisin.payments.domain.PaymentSplit
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.dto.*
import com.quisin.payments.repository.PaymentRepository
import com.quisin.payments.repository.PaymentSplitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val paymentSplitRepository: PaymentSplitRepository,
    private val paymentGatewayService: PaymentGatewayService,
    private val eventPublisher: PaymentEventPublisher
) {
    @Transactional
    fun createPayment(request: CreatePaymentRequest): PaymentResponse {
        // Check if payment for this order already exists
        val existingPayment = paymentRepository.findByUserIdAndOrderId(request.userId, request.orderId)
        if (existingPayment != null) {
            throw PaymentException("Payment for this order already exists")
        }

        // Process payment through payment gateway
        val gatewayResponse = paymentGatewayService.processPayment(
            amount = request.amount,
            method = request.paymentMethod,
            userId = request.userId
        )

        val payment = Payment(
            userId = request.userId,
            orderId = request.orderId,
            paymentMethod = request.paymentMethod,
            paymentType = request.paymentType,
            amount = request.amount,
            currency = request.currency,
            description = request.description,
            externalTransactionId = gatewayResponse.transactionId,
            status = gatewayResponse.status,
            metadata = request.metadata
        )

        val savedPayment = paymentRepository.save(payment)
        eventPublisher.publishPaymentEvent(savedPayment)
        return mapToPaymentResponse(savedPayment)
    }

    @Transactional
    fun createSplitPayment(request: CreateSplitPaymentRequest): PaymentResponse {
        // Validate total amount matches order total
        val totalAmount = request.participants.sumOf { it.amount }
        
        val payment = Payment(
            userId = request.participants.first().userId,
            orderId = request.orderId,
            paymentMethod = request.paymentMethod,
            paymentType = PaymentType.SPLIT_BILL,
            amount = totalAmount,
            currency = "USD",
            participants = request.participants.map { it.userId }
        )

        val savedPayment = paymentRepository.save(payment)

        // Create individual payment splits
        val paymentSplits = request.participants.map { participant ->
            PaymentSplit(
                paymentId = savedPayment.id!!,
                userId = participant.userId,
                amount = participant.amount
            )
        }

        paymentSplitRepository.saveAll(paymentSplits)
        eventPublisher.publishPaymentEvent(savedPayment)
        return mapToPaymentResponse(savedPayment)
    }

    @Transactional
    fun updatePaymentStatus(paymentId: UUID, request: UpdatePaymentRequest): PaymentResponse {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { PaymentException("Payment not found") }

        request.status?.let { payment.status = it }
        request.externalTransactionId?.let { payment.externalTransactionId = it }
        request.metadata?.let { payment.metadata = it }

        val updatedPayment = paymentRepository.save(payment)
        eventPublisher.publishPaymentEvent(updatedPayment)
        return mapToPaymentResponse(updatedPayment)
    }

    fun getPendingCashPayments(restaurantId: UUID): List<PaymentResponse> {
        return paymentRepository.findByRestaurantIdAndMethodAndStatus(
            restaurantId,
            PaymentMethod.CASH,
            PaymentStatus.PENDING
        ).map { mapToPaymentResponse(it) }
    }

    fun getPaymentsByUser(userId: UUID, status: PaymentStatus? = null): List<PaymentResponse> {
        val payments = status?.let { 
            paymentRepository.findByUserIdAndStatus(userId, it) 
        } ?: paymentRepository.findAll().filter { it.userId == userId }

        return payments.map { mapToPaymentResponse(it) }
    }

    fun getPaymentSplits(paymentId: UUID): List<PaymentSplitResponse> {
        return paymentSplitRepository.findByPaymentId(paymentId)
            .map { mapToPaymentSplitResponse(it) }
    }

    private fun mapToPaymentResponse(payment: Payment): PaymentResponse {
        return PaymentResponse(
            id = payment.id!!,
            userId = payment.userId,
            orderId = payment.orderId,
            paymentMethod = payment.paymentMethod,
            paymentType = payment.paymentType,
            status = payment.status,
            amount = payment.amount,
            currency = payment.currency,
            description = payment.description,
            externalTransactionId = payment.externalTransactionId,
            metadata = payment.metadata,
            createdAt = payment.createdAt!!,
            updatedAt = payment.updatedAt
        )
    }

    private fun mapToPaymentSplitResponse(paymentSplit: PaymentSplit): PaymentSplitResponse {
        return PaymentSplitResponse(
            id = paymentSplit.id!!,
            paymentId = paymentSplit.paymentId,
            userId = paymentSplit.userId,
            amount = paymentSplit.amount,
            status = paymentSplit.status
        )
    }
}

class PaymentException(message: String) : RuntimeException(message)

// External Payment Gateway Service (Placeholder)
interface PaymentGatewayService {
    data class PaymentGatewayResponse(
        val transactionId: String,
        val status: PaymentStatus
    )

    fun processPayment(
        amount: BigDecimal, 
        method: PaymentMethod, 
        userId: UUID
    ): PaymentGatewayResponse
}
