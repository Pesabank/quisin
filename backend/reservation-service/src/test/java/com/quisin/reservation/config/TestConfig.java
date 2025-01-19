package com.quisin.reservation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

@TestConfiguration
@EmbeddedKafka(partitions = 1, topics = {"reservation-events"})
public class TestConfig {

    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        return new EmbeddedKafkaBroker(1)
            .kafkaPorts(9092)
            .brokerProperty("listeners", "PLAINTEXT://localhost:9092")
            .brokerProperty("auto.create.topics.enable", "true");
    }
} 