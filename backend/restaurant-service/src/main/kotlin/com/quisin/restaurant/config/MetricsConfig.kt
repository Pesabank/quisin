package com.quisin.restaurant.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                listOf(
                    Tag.of("application", "restaurant-service"),
                    Tag.of("env", System.getenv("SPRING_PROFILES_ACTIVE") ?: "default")
                )
            )
        }
    }
} 