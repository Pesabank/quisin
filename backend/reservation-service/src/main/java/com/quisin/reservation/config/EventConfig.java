package com.quisin.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import java.util.function.Consumer;

@Configuration
public class EventConfig {

    @Bean
    public Consumer<Message<String>> reservationEventConsumer() {
        return message -> {
            // Log the received message
            System.out.println("Received message: " + message.getPayload());
            
            // TODO: Implement event handling logic for incoming messages
            // This will be used when we need to handle events from other services
        };
    }
} 