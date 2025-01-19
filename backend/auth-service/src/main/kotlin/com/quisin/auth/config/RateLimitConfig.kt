package com.quisin.auth.config

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitConfig {

    @Bean
    fun authenticationRateLimiter(): RateLimiter {
        val config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(10)  // 10 requests per minute
            .timeoutDuration(Duration.ofSeconds(3))
            .build()

        return RateLimiterRegistry.of(config).rateLimiter("authentication")
    }

    @Bean
    fun registrationRateLimiter(): RateLimiter {
        val config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofHours(1))
            .limitForPeriod(5)  // 5 registrations per hour
            .timeoutDuration(Duration.ofSeconds(3))
            .build()

        return RateLimiterRegistry.of(config).rateLimiter("registration")
    }
}
