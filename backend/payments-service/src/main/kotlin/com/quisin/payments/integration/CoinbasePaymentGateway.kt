package com.quisin.payments.integration

import com.fasterxml.jackson.annotation.JsonProperty
import com.quisin.payments.domain.PaymentMethod
import com.quisin.payments.domain.PaymentStatus
import com.quisin.payments.service.PaymentGatewayService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.util.UUID

@Service
class CoinbasePaymentGateway(
    private val restTemplate: RestTemplate,
    @Value("\${coinbase.api-key}") private val apiKey: String,
    @Value("\${coinbase.api-secret}") private val apiSecret: String
) : PaymentGatewayService {

    companion object {
        private const val BASE_URL = "https://api.commerce.coinbase.com"
    }

    override fun processPayment(
        amount: BigDecimal, 
        method: PaymentMethod, 
        userId: UUID
    ): PaymentGatewayService.PaymentGatewayResponse {
        // Validate method is CRYPTOCURRENCY
        require(method == PaymentMethod.CRYPTOCURRENCY) { "Invalid payment method for Coinbase" }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("X-CC-Api-Key", apiKey)
            set("X-CC-Version", "2018-03-22")
        }

        val chargeRequest = CoinbaseChargeRequest(
            name = "Quisin Restaurant Payment",
            description = "Payment for restaurant order - User $userId",
            pricing_type = "fixed_price",
            local_price = LocalPrice(
                amount = amount.toPlainString(),
                currency = "USD"
            )
        )

        val response = restTemplate.exchange(
            "$BASE_URL/charges",
            HttpMethod.POST,
            HttpEntity(chargeRequest, headers),
            CoinbaseChargeResponse::class.java
        )

        return PaymentGatewayService.PaymentGatewayResponse(
            transactionId = response.body?.data?.id ?: UUID.randomUUID().toString(),
            status = when (response.body?.data?.timeline?.lastOrNull()?.status) {
                "NEW" -> PaymentStatus.PENDING
                "COMPLETED" -> PaymentStatus.SUCCESSFUL
                "FAILED" -> PaymentStatus.FAILED
                else -> PaymentStatus.PENDING
            }
        )
    }
}

data class CoinbaseChargeRequest(
    val name: String,
    val description: String,
    val pricing_type: String,
    val local_price: LocalPrice
)

data class LocalPrice(
    val amount: String,
    val currency: String
)

data class CoinbaseChargeResponse(
    val data: CoinbaseChargeData?
)

data class CoinbaseChargeData(
    val id: String?,
    val timeline: List<CoinbaseChargeTimeline>?
)

data class CoinbaseChargeTimeline(
    val status: String?
)
