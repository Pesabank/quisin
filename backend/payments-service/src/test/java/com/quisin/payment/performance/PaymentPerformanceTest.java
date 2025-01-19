package com.quisin.payment.performance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 10;
    private static final Duration MAX_ACCEPTABLE_DURATION = Duration.ofSeconds(30);

    @Test
    public void testConcurrentCardPayments() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<PaymentMetrics> metrics = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Instant startTime = Instant.now();

        // Submit concurrent payment requests
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < REQUESTS_PER_USER; j++) {
                    try {
                        Instant requestStart = Instant.now();
                        var response = mockMvc.perform(post("/api/payments/card")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createCardPaymentRequest()))
                                .andReturn();
                        
                        Instant requestEnd = Instant.now();
                        Duration requestDuration = Duration.between(requestStart, requestEnd);
                        
                        synchronized (metrics) {
                            metrics.add(new PaymentMetrics(requestDuration, response.getResponse().getStatus()));
                        }

                        if (response.getResponse().getStatus() == 200) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                    }
                }
            });
        }

        // Shutdown executor and wait for completion
        executor.shutdown();
        boolean completed = executor.awaitTermination(1, TimeUnit.MINUTES);
        assertTrue(completed, "Performance test did not complete within timeout");

        Instant endTime = Instant.now();
        Duration totalDuration = Duration.between(startTime, endTime);

        // Calculate metrics
        double avgResponseTime = metrics.stream()
                .mapToLong(m -> m.duration.toMillis())
                .average()
                .orElse(0.0);

        double successRate = (double) successCount.get() / (CONCURRENT_USERS * REQUESTS_PER_USER) * 100;

        // Assert performance requirements
        assertTrue(totalDuration.compareTo(MAX_ACCEPTABLE_DURATION) <= 0,
                "Test took too long: " + totalDuration.toSeconds() + " seconds");
        assertTrue(avgResponseTime < 1000, "Average response time too high: " + avgResponseTime + "ms");
        assertTrue(successRate >= 95, "Success rate too low: " + successRate + "%");

        System.out.println("Performance Test Results:");
        System.out.println("Total Duration: " + totalDuration.toSeconds() + " seconds");
        System.out.println("Average Response Time: " + avgResponseTime + "ms");
        System.out.println("Success Rate: " + successRate + "%");
        System.out.println("Total Requests: " + (CONCURRENT_USERS * REQUESTS_PER_USER));
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failureCount.get());
    }

    private String createCardPaymentRequest() throws Exception {
        var request = new CardPaymentRequest(
            "4242424242424242",
            "12/25",
            "123",
            "Test User",
            "ORDER" + System.nanoTime(), // Ensure unique order IDs
            1000L
        );
        return objectMapper.writeValueAsString(request);
    }

    private record PaymentMetrics(Duration duration, int statusCode) {}

    private record CardPaymentRequest(
        String cardNumber,
        String expiry,
        String cvc,
        String cardHolderName,
        String orderId,
        Long amount
    ) {}
} 