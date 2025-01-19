package com.quisin.payments.repository

import com.quisin.payments.domain.Payment
import com.quisin.payments.domain.PaymentSplit
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.domain.PaymentType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByUserIdAndOrderId(userId: UUID, orderId: UUID): Payment?
    
    fun findByOrderId(orderId: UUID): List<Payment>
    
    fun findByUserIdAndStatus(userId: UUID, status: PaymentStatus): List<Payment>
    
    fun findByPaymentTypeAndCreatedAtBetween(
        paymentType: PaymentType, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<Payment>
}

@Repository
interface PaymentSplitRepository : JpaRepository<PaymentSplit, UUID> {
    fun findByPaymentId(paymentId: UUID): List<PaymentSplit>
    
    fun findByUserId(userId: UUID): List<PaymentSplit>
    
    fun findByPaymentIdAndStatus(paymentId: UUID, status: PaymentStatus): List<PaymentSplit>
}
