package com.quisin.analytics.service

import com.quisin.payments.domain.PaymentMethod
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class PaymentAnalyticsService(
    private val paymentRepository: PaymentRepository
) {
    data class PaymentMethodAnalytics(
        val method: PaymentMethod,
        val totalTransactions: Int,
        val totalAmount: BigDecimal,
        val successRate: Double
    )

    fun getPaymentMethodAnalytics(
        startDate: LocalDateTime = LocalDateTime.now().minusMonths(1),
        endDate: LocalDateTime = LocalDateTime.now()
    ): List<PaymentMethodAnalytics> {
        // Retrieve all payments within the specified date range
        val payments = paymentRepository.findAll()
            .filter { 
                it.createdAt != null && 
                it.createdAt!! >= startDate && 
                it.createdAt!! <= endDate 
            }

        // Group payments by method and calculate analytics
        return payments.groupBy { it.paymentMethod }
            .map { (method, methodPayments) ->
                PaymentMethodAnalytics(
                    method = method,
                    totalTransactions = methodPayments.size,
                    totalAmount = methodPayments.sumOf { it.amount },
                    successRate = methodPayments.count { it.status == PaymentStatus.SUCCESSFUL }.toDouble() / methodPayments.size
                )
            }
            .sortedByDescending { it.totalAmount }
    }

    fun getTopPaymentMethods(limit: Int = 5): List<PaymentMethodAnalytics> {
        return getPaymentMethodAnalytics().take(limit)
    }

    fun comparePaymentMethodPerformance(): Map<PaymentMethod, Double> {
        return getPaymentMethodAnalytics()
            .associate { it.method to it.successRate }
    }

    fun predictPaymentTrends(): List<PaymentMethodAnalytics> {
        val currentAnalytics = getPaymentMethodAnalytics()
        val previousAnalytics = getPaymentMethodAnalytics(
            startDate = LocalDateTime.now().minusMonths(2),
            endDate = LocalDateTime.now().minusMonths(1)
        )

        return currentAnalytics.map { current ->
            val previous = previousAnalytics.find { it.method == current.method }
            
            current.copy(
                totalTransactions = current.totalTransactions,
                totalAmount = current.totalAmount,
                successRate = calculateTrendPercentage(
                    current.successRate, 
                    previous?.successRate ?: 0.0
                )
            )
        }.sortedByDescending { it.successRate }
    }

    private fun calculateTrendPercentage(current: Double, previous: Double): Double {
        return if (previous == 0.0) current 
        else ((current - previous) / previous) * 100
    }
}
