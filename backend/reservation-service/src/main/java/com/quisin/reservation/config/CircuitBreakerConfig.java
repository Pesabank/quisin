package com.quisin.reservation.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreakerConfigCustomizer restaurantServiceCircuitBreakerConfig() {
        return CircuitBreakerConfigCustomizer
            .of("restaurantService", builder -> builder
                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(5)
                .minimumNumberOfCalls(5)
                .recordExceptions(Exception.class)
            );
    }
} 