package com.quisin.payments.service

import com.quisin.payments.domain.Payment
import com.quisin.payments.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
class FraudDetectionService(
    private val paymentRepository: PaymentRepository
) {
    data class FraudRiskAssessment(
        val riskScore: Double,
        val flags: List<String>,
        val recommendation: FraudRecommendation
    )

    enum class FraudRecommendation {
        APPROVE,
        REVIEW,
        BLOCK
    }

    fun assessTransactionRisk(
        userId: UUID,
        amount: BigDecimal,
        paymentMethod: String
    ): FraudRiskAssessment {
        val flags = mutableListOf<String>()
        var riskScore = 0.0

        // 1. Unusual Transaction Amount
        riskScore += assessTransactionAmount(amount, userId)
        
        // 2. Frequency of Transactions
        riskScore += assessTransactionFrequency(userId)
        
        // 3. Geographic Inconsistency
        riskScore += assessGeographicConsistency(userId)
        
        // 4. Payment Method Risk
        riskScore += assessPaymentMethodRisk(paymentMethod)
        
        // 5. Historical Payment Behavior
        riskScore += assessHistoricalBehavior(userId)

        // Determine recommendation based on risk score
        val recommendation = when {
            riskScore > 80 -> FraudRecommendation.BLOCK
            riskScore > 50 -> FraudRecommendation.REVIEW
            else -> FraudRecommendation.APPROVE
        }

        return FraudRiskAssessment(
            riskScore = riskScore,
            flags = flags,
            recommendation = recommendation
        )
    }

    private fun assessTransactionAmount(amount: BigDecimal, userId: UUID): Double {
        val userPayments = paymentRepository.findByUserIdAndStatus(userId, null)
        val averageAmount = userPayments.map { it.amount }.average().orElse(0.0)
        
        return when {
            amount.toDouble() > averageAmount * 3 -> 30.0
            amount.toDouble() > averageAmount * 2 -> 20.0
            else -> 0.0
        }
    }

    private fun assessTransactionFrequency(userId: UUID): Double {
        val recentPayments = paymentRepository.findByUserIdAndStatus(userId, null)
            .filter { it.createdAt?.isAfter(LocalDateTime.now().minusDays(1)) ?: false }
        
        return when {
            recentPayments.size > 5 -> 25.0
            recentPayments.size > 3 -> 15.0
            else -> 0.0
        }
    }

    private fun assessGeographicConsistency(userId: UUID): Double {
        // Placeholder for more sophisticated geo-tracking
        // Would typically involve checking IP addresses, device locations
        return 0.0
    }

    private fun assessPaymentMethodRisk(paymentMethod: String): Double {
        return when (paymentMethod) {
            "CRYPTOCURRENCY" -> 20.0
            "MOBILE_MONEY" -> 10.0
            else -> 5.0
        }
    }

    private fun assessHistoricalBehavior(userId: UUID): Double {
        val userPayments = paymentRepository.findByUserIdAndStatus(userId, null)
        
        val failedPaymentsRatio = userPayments
            .count { it.status != null && it.status.toString().contains("FAILED") }
            .toDouble() / userPayments.size

        return failedPaymentsRatio * 25.0
    }

    fun blockTransaction(paymentId: UUID, reason: String) {
        // Implement transaction blocking logic
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { IllegalArgumentException("Payment not found") }
        
        // Log fraud attempt
        logFraudAttempt(payment, reason)
    }

    private fun logFraudAttempt(payment: Payment, reason: String) {
        // Implement logging mechanism
        // Could involve database logging, external fraud reporting system, etc.
    }
}
