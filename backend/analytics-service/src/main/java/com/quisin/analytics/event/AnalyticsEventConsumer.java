package com.quisin.analytics.event;

import com.quisin.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {
    private final AnalyticsService analyticsService;

    @StreamListener("orderEvents-in-0")
    public void handleOrderEvent(@Payload String event) {
        log.info("Received order event: {}", event);
        analyticsService.processOrderEvent(event);
    }

    @StreamListener("paymentEvents-in-0")
    public void handlePaymentEvent(@Payload String event) {
        log.info("Received payment event: {}", event);
        analyticsService.processPaymentEvent(event);
    }

    @StreamListener("reservationEvents-in-0")
    public void handleReservationEvent(@Payload String event) {
        log.info("Received reservation event: {}", event);
        analyticsService.processReservationEvent(event);
    }

    @StreamListener("reviewEvents-in-0")
    public void handleReviewEvent(@Payload String event) {
        log.info("Received review event: {}", event);
        analyticsService.processReviewEvent(event);
    }
} 