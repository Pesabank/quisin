package com.quisin.restaurant.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ResilienceConfig {

    @Bean
    fun defaultCustomizer(): Customizer<Resilience4JCircuitBreakerFactory> {
        return Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .timeLimiterConfig(timeLimiterConfig())
                    .circuitBreakerConfig(circuitBreakerConfig())
                    .build()
            }
        }
    }

    private fun timeLimiterConfig(): TimeLimiterConfig {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(3))
            .build()
    }

    private fun circuitBreakerConfig(): CircuitBreakerConfig {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .slidingWindowSize(5)
            .minimumNumberOfCalls(3)
            .build()
    }
} 