package com.quisin.payments.event

import com.quisin.payments.domain.Payment
import org.springframework.cloud.stream.function.StreamsBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Component
class PaymentEventPublisher(
    private val streamsBuilder: StreamsBuilder
) {
    fun publishPaymentEvent(payment: Payment) {
        val event = PaymentEvent(
            eventId = UUID.randomUUID(),
            eventType = getEventType(payment),
            paymentId = payment.id!!,
            orderId = payment.orderId,
            userId = payment.userId,
            status = payment.status,
            amount = payment.amount,
            currency = payment.currency,
            paymentMethod = payment.paymentMethod,
            timestamp = LocalDateTime.now()
        )

        streamsBuilder.stream("payment-events")
            .send(MessageBuilder.withPayload(event).build())
    }

    private fun getEventType(payment: Payment): PaymentEventType {
        return when (payment.status) {
            PaymentStatus.PENDING -> PaymentEventType.PAYMENT_INITIATED
            PaymentStatus.SUCCESSFUL -> PaymentEventType.PAYMENT_SUCCESSFUL
            PaymentStatus.FAILED -> PaymentEventType.PAYMENT_FAILED
            PaymentStatus.REFUNDED -> PaymentEventType.PAYMENT_REFUNDED
            PaymentStatus.CANCELLED -> PaymentEventType.PAYMENT_CANCELLED
        }
    }
}

@Configuration
class PaymentEventConfig {
    @Bean
    fun paymentEventProducer(): (PaymentEvent) -> Unit = { event ->
        // Additional event processing logic if needed
    }
}

data class PaymentEvent(
    val eventId: UUID,
    val eventType: PaymentEventType,
    val paymentId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val status: PaymentStatus,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: PaymentMethod,
    val timestamp: LocalDateTime
)

enum class PaymentEventType {
    PAYMENT_INITIATED,
    PAYMENT_SUCCESSFUL,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,
    PAYMENT_CANCELLED
} 