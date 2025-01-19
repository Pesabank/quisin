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
import java.util.Base64
import java.util.UUID

@Service
class MpesaPaymentGateway(
    private val restTemplate: RestTemplate,
    @Value("\${mpesa.consumer-key}") private val consumerKey: String,
    @Value("\${mpesa.consumer-secret}") private val consumerSecret: String,
    @Value("\${mpesa.environment}") private val environment: String
) : PaymentGatewayService {

    companion object {
        private const val SANDBOX_BASE_URL = "https://sandbox.safaricom.co.ke"
        private const val PRODUCTION_BASE_URL = "https://api.safaricom.co.ke"
    }

    override fun processPayment(
        amount: BigDecimal, 
        method: PaymentMethod, 
        userId: UUID
    ): PaymentGatewayService.PaymentGatewayResponse {
        // Validate method is MOBILE_MONEY
        require(method == PaymentMethod.MOBILE_MONEY) { "Invalid payment method for M-Pesa" }

        // Get access token
        val accessToken = getAccessToken()

        // Prepare STK push request
        val stkPushRequest = prepareStkPushRequest(amount, userId)

        // Send STK push request
        val stkPushResponse = sendStkPushRequest(accessToken, stkPushRequest)

        // Map response to payment gateway response
        return PaymentGatewayService.PaymentGatewayResponse(
            transactionId = stkPushResponse.checkoutRequestId ?: UUID.randomUUID().toString(),
            status = when {
                stkPushResponse.responseCode == "0" -> PaymentStatus.PENDING
                else -> PaymentStatus.FAILED
            }
        )
    }

    private fun getAccessToken(): String {
        val credentials = Base64.getEncoder().encodeToString("$consumerKey:$consumerSecret".toByteArray())
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Basic $credentials")
        }

        val baseUrl = if (environment == "production") PRODUCTION_BASE_URL else SANDBOX_BASE_URL
        val response = restTemplate.exchange(
            "$baseUrl/oauth/v1/generate?grant_type=client_credentials",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            AccessTokenResponse::class.java
        )

        return response.body?.accessToken 
            ?: throw RuntimeException("Failed to obtain M-Pesa access token")
    }

    private fun prepareStkPushRequest(amount: BigDecimal, userId: UUID): StkPushRequest {
        val timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        
        return StkPushRequest(
            businessShortCode = "174379", // Default Safaricom Lipa na M-Pesa Online shortcode
            password = generatePassword(timestamp),
            timestamp = timestamp,
            transactionType = "CustomerPayBillOnline",
            amount = amount.toPlainString(),
            phoneNumber = "254708374149", // Placeholder - will be replaced with actual user phone
            callbackUrl = "https://quisin.com/mpesa/callback",
            accountReference = "Quisin Payment - $userId",
            transactionDescription = "Quisin Restaurant Payment"
        )
    }

    private fun sendStkPushRequest(accessToken: String, request: StkPushRequest): StkPushResponse {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val baseUrl = if (environment == "production") PRODUCTION_BASE_URL else SANDBOX_BASE_URL
        val response = restTemplate.exchange(
            "$baseUrl/mpesa/stkpush/v1/processrequest",
            HttpMethod.POST,
            HttpEntity(request, headers),
            StkPushResponse::class.java
        )

        return response.body 
            ?: throw RuntimeException("Failed to send M-Pesa STK push")
    }

    private fun generatePassword(timestamp: String): String {
        val combinedString = "174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + timestamp
        return Base64.getEncoder().encodeToString(combinedString.toByteArray())
    }
}

// DTOs for M-Pesa API
data class AccessTokenResponse(
    @JsonProperty("access_token") val accessToken: String?,
    @JsonProperty("expires_in") val expiresIn: String?
)

data class StkPushRequest(
    @JsonProperty("BusinessShortCode") val businessShortCode: String,
    @JsonProperty("Password") val password: String,
    @JsonProperty("Timestamp") val timestamp: String,
    @JsonProperty("TransactionType") val transactionType: String,
    @JsonProperty("Amount") val amount: String,
    @JsonProperty("PartyA") val phoneNumber: String,
    @JsonProperty("PartyB") val businessPartyB: String = "174379",
    @JsonProperty("PhoneNumber") val customerPhoneNumber: String = phoneNumber,
    @JsonProperty("CallBackURL") val callbackUrl: String,
    @JsonProperty("AccountReference") val accountReference: String,
    @JsonProperty("TransactionDesc") val transactionDescription: String
)

data class StkPushResponse(
    @JsonProperty("MerchantRequestID") val merchantRequestId: String?,
    @JsonProperty("CheckoutRequestID") val checkoutRequestId: String?,
    @JsonProperty("ResponseCode") val responseCode: String?,
    @JsonProperty("ResponseDescription") val responseDescription: String?
)
