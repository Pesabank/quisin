package com.quisin.payments.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    MOBILE_MONEY,
    BANK_TRANSFER,
    DIGITAL_WALLET,
    CRYPTOCURRENCY,
    CASH
}

enum class PaymentStatus {
    PENDING,
    SUCCESSFUL,
    FAILED,
    REFUNDED,
    CANCELLED
}

enum class PaymentType {
    SINGLE_ORDER,
    GROUP_ORDER,
    SPLIT_BILL,
    RESERVATION_FEE
}

@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener::class)
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val orderId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val paymentMethod: PaymentMethod,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val paymentType: PaymentType,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Column(nullable = false)
    val currency: String,

    @ElementCollection
    @CollectionTable(name = "payment_participants", joinColumns = [JoinColumn(name = "payment_id")])
    val participants: List<UUID> = listOf(),

    @Column(length = 500)
    val description: String? = null,

    @Column
    val externalTransactionId: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)

@Entity
@Table(name = "payment_splits")
data class PaymentSplit(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val paymentId: UUID,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.PENDING
)
