package com.quisin.payments.integration

import com.paypal.api.payments.*
import com.paypal.base.rest.APIContext
import com.quisin.payments.domain.PaymentMethod
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.service.PaymentGatewayService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class PayPalPaymentGateway(
    @Value("\${paypal.client-id}") private val clientId: String,
    @Value("\${paypal.client-secret}") private val clientSecret: String,
    @Value("\${paypal.mode}") private val mode: String
) : PaymentGatewayService {

    private fun getApiContext(): APIContext {
        return APIContext(clientId, clientSecret, mode)
    }

    override fun processPayment(
        amount: BigDecimal, 
        method: PaymentMethod, 
        userId: UUID
    ): PaymentGatewayService.PaymentGatewayResponse {
        require(method == PaymentMethod.DIGITAL_WALLET) { "Invalid payment method for PayPal" }

        return try {
            val apiContext = getApiContext()

            // Create payment details
            val details = Details().apply {
                subtotal = amount.toString()
                tax = "0"
                shipping = "0"
            }

            // Create amount
            val paypalAmount = Amount().apply {
                total = amount.toString()
                currency = "USD"
                details = details
            }

            // Create transaction
            val transaction = Transaction().apply {
                amount = paypalAmount
                description = "Quisin Restaurant Payment - User $userId"
            }

            // Create payment
            val payment = Payment().apply {
                intent = "sale"
                payer = Payer().apply { paymentMethod = "paypal" }
                transactions = listOf(transaction)
                redirectUrls = RedirectUrls().apply {
                    cancelUrl = "https://quisin.com/payment/cancel"
                    returnUrl = "https://quisin.com/payment/success"
                }
            }

            // Create payment
            val createdPayment = payment.create(apiContext)

            PaymentGatewayService.PaymentGatewayResponse(
                transactionId = createdPayment.id,
                status = when (createdPayment.state) {
                    "created" -> PaymentStatus.PENDING
                    "approved" -> PaymentStatus.SUCCESSFUL
                    "failed" -> PaymentStatus.FAILED
                    else -> PaymentStatus.PENDING
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
