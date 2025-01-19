package com.quisin.payments.service

import com.quisin.payments.domain.Payment
import com.quisin.payments.domain.PaymentStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class TransactionMonitoringService(
    private val eventPublisher: ApplicationEventPublisher,
    private val fraudDetectionService: FraudDetectionService
) {
    // In-memory store for recent transactions
    private val recentTransactions = ConcurrentHashMap<String, TransactionMonitoringEvent>()

    data class TransactionMonitoringEvent(
        val paymentId: String,
        val userId: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
        val status: PaymentStatus,
        val paymentMethod: String
    )

    @Async
    fun monitorTransaction(payment: Payment) {
        val event = TransactionMonitoringEvent(
            paymentId = payment.id.toString(),
            userId = payment.userId.toString(),
            amount = payment.amount,
            timestamp = LocalDateTime.now(),
            status = payment.status,
            paymentMethod = payment.paymentMethod.toString()
        )

        // Store recent transaction
        recentTransactions[event.paymentId] = event

        // Perform real-time risk assessment
        val riskAssessment = fraudDetectionService.assessTransactionRisk(
            payment.userId,
            payment.amount,
            payment.paymentMethod.toString()
        )

        // Publish monitoring event
        eventPublisher.publishEvent(event)

        // Handle high-risk transactions
        if (riskAssessment.recommendation != FraudDetectionService.FraudRecommendation.APPROVE) {
            handleHighRiskTransaction(event, riskAssessment)
        }

        // Clean up old transactions
        cleanupOldTransactions()
    }

    private fun handleHighRiskTransaction(
        event: TransactionMonitoringEvent, 
        riskAssessment: FraudDetectionService.FraudRiskAssessment
    ) {
        when (riskAssessment.recommendation) {
            FraudDetectionService.FraudRecommendation.REVIEW -> {
                // Trigger manual review process
                triggerManualReview(event, riskAssessment)
            }
            FraudDetectionService.FraudRecommendation.BLOCK -> {
                // Block the transaction
                blockTransaction(event, riskAssessment)
            }
            else -> {} // Approve transaction
        }
    }

    private fun triggerManualReview(
        event: TransactionMonitoringEvent, 
        riskAssessment: FraudDetectionService.FraudRiskAssessment
    ) {
        // Send notification to fraud team
        // Could integrate with notification service
        println("Transaction requires manual review: ${event.paymentId}")
        println("Risk Score: ${riskAssessment.riskScore}")
        println("Flags: ${riskAssessment.flags}")
    }

    private fun blockTransaction(
        event: TransactionMonitoringEvent, 
        riskAssessment: FraudDetectionService.FraudRiskAssessment
    ) {
        // Block transaction and log fraud attempt
        println("Blocking high-risk transaction: ${event.paymentId}")
        fraudDetectionService.blockTransaction(
            UUID.fromString(event.paymentId), 
            "High fraud risk: ${riskAssessment.flags}"
        )
    }

    private fun cleanupOldTransactions() {
        val cutoffTime = LocalDateTime.now().minusHours(24)
        recentTransactions.entries.removeIf { 
            it.value.timestamp.isBefore(cutoffTime) 
        }
    }

    fun getRecentTransactions(): List<TransactionMonitoringEvent> {
        return recentTransactions.values.toList()
    }

    fun getTransactionHistory(userId: String): List<TransactionMonitoringEvent> {
        return recentTransactions.values
            .filter { it.userId == userId }
            .sortedByDescending { it.timestamp }
    }
}
