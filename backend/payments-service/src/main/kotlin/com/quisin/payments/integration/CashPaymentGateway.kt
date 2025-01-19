package com.quisin.payments.integration

import com.quisin.payments.domain.PaymentMethod
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.service.PaymentGatewayService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class CashPaymentGateway : PaymentGatewayService {
    override fun processPayment(
        amount: BigDecimal,
        method: PaymentMethod,
        userId: UUID
    ): PaymentGatewayService.PaymentGatewayResponse {
        require(method == PaymentMethod.CASH) { "Invalid payment method for Cash payment" }
        require(amount > BigDecimal.ZERO) { "Amount must be greater than zero" }

        return PaymentGatewayService.PaymentGatewayResponse(
            transactionId = UUID.randomUUID().toString(),
            status = PaymentStatus.PENDING  // Initial state, will be updated to SUCCESSFUL when cash is received
        )
    }
} 