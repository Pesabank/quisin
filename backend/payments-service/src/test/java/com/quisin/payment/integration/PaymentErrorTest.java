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
public class PaymentErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCardPaymentDeclined() throws Exception {
        // Test card payment with declined card
        var paymentRequest = new CardPaymentRequest(
            "4000000000000002", // Stripe test card number for decline
            "12/25",
            "123",
            "Test User",
            "ORDER123",
            1000L
        );

        mockMvc.perform(post("/api/payments/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Card was declined"));
    }

    @Test
    public void testMpesaPaymentInvalidPhone() throws Exception {
        // Test M-PESA payment with invalid phone number
        var paymentRequest = new MpesaPaymentRequest(
            "254999999999", // Invalid phone number
            "ORDER123",
            1000L
        );

        mockMvc.perform(post("/api/payments/mpesa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid phone number format"));
    }

    @Test
    public void testPayPalPaymentInsufficientFunds() throws Exception {
        // Test PayPal payment with insufficient funds
        var paymentRequest = new PayPalPaymentRequest(
            "ORDER123",
            999999L // Large amount to trigger insufficient funds
        );

        mockMvc.perform(post("/api/payments/paypal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient funds"));
    }

    @Test
    public void testInvalidOrderId() throws Exception {
        // Test payment with non-existent order ID
        var paymentRequest = new CardPaymentRequest(
            "4242424242424242",
            "12/25",
            "123",
            "Test User",
            "INVALID_ORDER", // Non-existent order ID
            1000L
        );

        mockMvc.perform(post("/api/payments/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found"));
    }

    @Test
    public void testDuplicatePayment() throws Exception {
        // Test duplicate payment for the same order
        var paymentRequest = new CardPaymentRequest(
            "4242424242424242",
            "12/25",
            "123",
            "Test User",
            "PAID_ORDER", // Order that's already paid
            1000L
        );

        mockMvc.perform(post("/api/payments/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Payment already processed for this order"));
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
} 