package com.quisin.reservation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationEventPublisher {

    private final StreamBridge streamBridge;
    private static final String BINDING_NAME = "reservationEvents-out-0";

    public void publishReservationCreated(ReservationEvent event) {
        enrichAndPublishEvent(event, "RESERVATION_CREATED");
    }

    public void publishReservationUpdated(ReservationEvent event) {
        enrichAndPublishEvent(event, "RESERVATION_UPDATED");
    }

    public void publishReservationCancelled(ReservationEvent event) {
        enrichAndPublishEvent(event, "RESERVATION_CANCELLED");
    }

    public void publishReservationStatusChanged(ReservationEvent event) {
        enrichAndPublishEvent(event, "RESERVATION_STATUS_CHANGED");
    }

    private void enrichAndPublishEvent(ReservationEvent event, String eventType) {
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setTimestamp(LocalDateTime.now());
        
        streamBridge.send(BINDING_NAME, event);
    }
} 