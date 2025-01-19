package com.quisin.payments.integration

import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.param.ChargeCreateParams
import com.quisin.payments.domain.PaymentMethod
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.service.PaymentGatewayService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class StripePaymentGateway(
    @Value("\${stripe.api-key}") private val stripeApiKey: String
) : PaymentGatewayService {

    init {
        Stripe.apiKey = stripeApiKey
    }

    override fun processPayment(
        amount: BigDecimal, 
        method: PaymentMethod, 
        userId: UUID
    ): PaymentGatewayService.PaymentGatewayResponse {
        // Validate method is CREDIT_CARD or DEBIT_CARD
        require(method in listOf(PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD)) { 
            "Invalid payment method for Stripe" 
        }

        return try {
            val chargeParams = ChargeCreateParams.builder()
                .setAmount((amount.multiply(BigDecimal(100)).toLong())) // Convert to cents
                .setCurrency("usd")
                .setDescription("Quisin Restaurant Payment - User $userId")
                .setSource("tok_visa") // Placeholder - will be replaced with actual token
                .build()

            val charge = Charge.create(chargeParams)

            PaymentGatewayService.PaymentGatewayResponse(
                transactionId = charge.id,
                status = when (charge.status) {
                    "succeeded" -> PaymentStatus.SUCCESSFUL
                    "pending" -> PaymentStatus.PENDING
                    else -> PaymentStatus.FAILED
                }
            )
        } catch (e: Exception) {
            PaymentGatewayService.PaymentGatewayResponse(
                transactionId = UUID.randomUUID().toString(),
                status = PaymentStatus.FAILED
            )
        }
    }
}
