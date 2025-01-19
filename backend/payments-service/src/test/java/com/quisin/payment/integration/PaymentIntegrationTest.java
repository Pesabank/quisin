package com.quisin.payment.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCardPaymentIntegration() throws Exception {
        // Test card payment flow
        var paymentRequest = new CardPaymentRequest(
            "4242424242424242",
            "12/25",
            "123",
            "Test User",
            "ORDER123",
            1000L
        );

        mockMvc.perform(post("/api/payments/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    public void testMpesaPaymentIntegration() throws Exception {
        // Test M-PESA payment flow
        var paymentRequest = new MpesaPaymentRequest(
            "254712345678",
            "ORDER123",
            1000L
        );

        mockMvc.perform(post("/api/payments/mpesa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutRequestId").exists());
    }

    @Test
    public void testPayPalPaymentIntegration() throws Exception {
        // Test PayPal payment flow
        var paymentRequest = new PayPalPaymentRequest(
            "ORDER123",
            1000L
        );

        mockMvc.perform(post("/api/payments/paypal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paypalOrderId").exists());
    }

    @Test
    public void testCashPaymentIntegration() throws Exception {
        // Test cash payment flow
        var paymentRequest = new CashPaymentRequest(
            "ORDER123",
            1000L
        );

        mockMvc.perform(post("/api/payments/cash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentCode").exists());
    }

    // Request DTOs
    record CardPaymentRequest(
        String cardNumber,
        String expiry,
        String cvc,
        String cardHolderName,
        String orderId,
        Long amount
    ) {}

    record MpesaPaymentRequest(
        String phoneNumber,
        String orderId,
        Long amount
    ) {}

    record PayPalPaymentRequest(
        String orderId,
        Long amount
    ) {}

    record CashPaymentRequest(
        String orderId,
        Long amount
    ) {}
} 