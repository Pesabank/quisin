package com.quisin.review.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class EventConfig {

    @Bean
    public Consumer<Message<String>> reviewEventConsumer() {
        return message -> {
            log.debug("Received message: {}", message.getPayload());
            // TODO: Implement event handling logic for incoming messages
            // This will be used when we need to handle events from other services
        };
    }
} 