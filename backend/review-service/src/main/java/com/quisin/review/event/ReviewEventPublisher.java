package com.quisin.review.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventPublisher {

    private final StreamBridge streamBridge;
    private static final String BINDING_NAME = "reviewEvents-out-0";

    public void publishReviewCreated(ReviewEvent event) {
        enrichAndPublishEvent(event, "REVIEW_CREATED");
    }

    public void publishReviewUpdated(ReviewEvent event) {
        enrichAndPublishEvent(event, "REVIEW_UPDATED");
    }

    public void publishReviewModerated(ReviewEvent event) {
        enrichAndPublishEvent(event, "REVIEW_MODERATED");
    }

    public void publishReviewDeleted(ReviewEvent event) {
        enrichAndPublishEvent(event, "REVIEW_DELETED");
    }

    private void enrichAndPublishEvent(ReviewEvent event, String eventType) {
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setTimestamp(LocalDateTime.now());

        log.debug("Publishing {} event: {}", eventType, event);
        streamBridge.send(BINDING_NAME, event);
        log.info("Published {} event for review {}", eventType, event.getReviewId());
    }
} 