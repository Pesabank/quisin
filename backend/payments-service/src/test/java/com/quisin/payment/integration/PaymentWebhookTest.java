package com.quisin.payment.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentWebhookTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testStripeWebhook() throws Exception {
        // Create a mock Stripe webhook event
        ObjectNode eventData = objectMapper.createObjectNode()
            .put("id", "evt_test_webhook")
            .put("type", "payment_intent.succeeded")
            .put("created", System.currentTimeMillis() / 1000);

        ObjectNode paymentIntent = objectMapper.createObjectNode()
            .put("id", "pi_test_123")
            .put("status", "succeeded")
            .put("amount", 1000)
            .put("currency", "usd");

        eventData.set("data", objectMapper.createObjectNode()
            .set("object", paymentIntent));

        mockMvc.perform(post("/api/payments/stripe/webhook")
                .contentType("application/json")
                .header("Stripe-Signature", "mock_signature")
                .content(objectMapper.writeValueAsString(eventData)))
                .andExpect(status().isOk());
    }

    @Test
    public void testMpesaWebhook() throws Exception {
        // Create a mock M-PESA webhook callback
        ObjectNode callbackData = objectMapper.createObjectNode();
        ObjectNode body = objectMapper.createObjectNode()
            .put("ResultCode", "0")
            .put("ResultDesc", "The service request is processed successfully.")
            .put("MerchantRequestID", "123456-7890123-1")
            .put("CheckoutRequestID", "ws_CO_123456789")
            .put("Amount", "1000")
            .put("MpesaReceiptNumber", "PHJ234K671");

        callbackData.set("Body", body);

        mockMvc.perform(post("/api/payments/mpesa/callback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callbackData)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPayPalWebhook() throws Exception {
        // Create a mock PayPal webhook event
        ObjectNode eventData = objectMapper.createObjectNode()
            .put("id", "WH-TEST-123")
            .put("event_type", "PAYMENT.CAPTURE.COMPLETED")
            .put("create_time", "2024-01-10T10:00:00Z");

        ObjectNode resource = objectMapper.createObjectNode()
            .put("id", "5O190127TN364715T")
            .put("status", "COMPLETED")
            .put("amount", objectMapper.createObjectNode()
                .put("total", "10.00")
                .put("currency", "USD"));

        eventData.set("resource", resource);

        mockMvc.perform(post("/api/payments/paypal/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .header("PAYPAL-TRANSMISSION-ID", "mock_id")
                .header("PAYPAL-TRANSMISSION-TIME", "2024-01-10T10:00:00Z")
                .header("PAYPAL-TRANSMISSION-SIG", "mock_signature")
                .content(objectMapper.writeValueAsString(eventData)))
                .andExpect(status().isOk());
    }
} 