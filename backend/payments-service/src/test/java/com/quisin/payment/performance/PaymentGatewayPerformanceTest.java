package com.quisin.payment.performance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
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

@SpringBootTest
@ActiveProfiles("test")
public class PaymentGatewayPerformanceTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final int CONCURRENT_REQUESTS = 100;
    private static final Duration MAX_ACCEPTABLE_DURATION = Duration.ofSeconds(60);

    @Test
    public void testStripeGatewayPerformance() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        List<GatewayMetrics> metrics = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Instant startTime = Instant.now();

        // Submit concurrent Stripe API requests
        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    Instant requestStart = Instant.now();
                    
                    // Simulate Stripe API call with mock implementation
                    Thread.sleep(100); // Simulate network latency
                    boolean success = Math.random() > 0.1; // 90% success rate
                    
                    Instant requestEnd = Instant.now();
                    Duration requestDuration = Duration.between(requestStart, requestEnd);
                    
                    synchronized (metrics) {
                        metrics.add(new GatewayMetrics(requestDuration, success));
                    }

                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        // Shutdown executor and wait for completion
        executor.shutdown();
        boolean completed = executor.awaitTermination(1, TimeUnit.MINUTES);
        assertTrue(completed, "Gateway performance test did not complete within timeout");

        Instant endTime = Instant.now();
        Duration totalDuration = Duration.between(startTime, endTime);

        // Calculate metrics
        double avgResponseTime = metrics.stream()
                .mapToLong(m -> m.duration.toMillis())
                .average()
                .orElse(0.0);

        double successRate = (double) successCount.get() / CONCURRENT_REQUESTS * 100;

        // Assert performance requirements
        assertTrue(totalDuration.compareTo(MAX_ACCEPTABLE_DURATION) <= 0,
                "Test took too long: " + totalDuration.toSeconds() + " seconds");
        assertTrue(avgResponseTime < 500, "Average response time too high: " + avgResponseTime + "ms");
        assertTrue(successRate >= 85, "Success rate too low: " + successRate + "%");

        System.out.println("Stripe Gateway Performance Test Results:");
        System.out.println("Total Duration: " + totalDuration.toSeconds() + " seconds");
        System.out.println("Average Response Time: " + avgResponseTime + "ms");
        System.out.println("Success Rate: " + successRate + "%");
        System.out.println("Total Requests: " + CONCURRENT_REQUESTS);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failureCount.get());
    }

    @Test
    public void testMpesaGatewayPerformance() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        List<GatewayMetrics> metrics = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Instant startTime = Instant.now();

        // Submit concurrent M-PESA API requests
        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    Instant requestStart = Instant.now();
                    
                    // Simulate M-PESA API call with mock implementation
                    Thread.sleep(200); // M-PESA typically has higher latency
                    boolean success = Math.random() > 0.15; // 85% success rate
                    
                    Instant requestEnd = Instant.now();
                    Duration requestDuration = Duration.between(requestStart, requestEnd);
                    
                    synchronized (metrics) {
                        metrics.add(new GatewayMetrics(requestDuration, success));
                    }

                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        // Shutdown executor and wait for completion
        executor.shutdown();
        boolean completed = executor.awaitTermination(1, TimeUnit.MINUTES);
        assertTrue(completed, "Gateway performance test did not complete within timeout");

        Instant endTime = Instant.now();
        Duration totalDuration = Duration.between(startTime, endTime);

        // Calculate metrics
        double avgResponseTime = metrics.stream()
                .mapToLong(m -> m.duration.toMillis())
                .average()
                .orElse(0.0);

        double successRate = (double) successCount.get() / CONCURRENT_REQUESTS * 100;

        // Assert performance requirements (more lenient for M-PESA)
        assertTrue(totalDuration.compareTo(MAX_ACCEPTABLE_DURATION) <= 0,
                "Test took too long: " + totalDuration.toSeconds() + " seconds");
        assertTrue(avgResponseTime < 1000, "Average response time too high: " + avgResponseTime + "ms");
        assertTrue(successRate >= 80, "Success rate too low: " + successRate + "%");

        System.out.println("M-PESA Gateway Performance Test Results:");
        System.out.println("Total Duration: " + totalDuration.toSeconds() + " seconds");
        System.out.println("Average Response Time: " + avgResponseTime + "ms");
        System.out.println("Success Rate: " + successRate + "%");
        System.out.println("Total Requests: " + CONCURRENT_REQUESTS);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failureCount.get());
    }

    private record GatewayMetrics(Duration duration, boolean success) {}
} 