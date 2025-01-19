package com.quisin.restaurant.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitConfig {

    @Value("\${quisin.rate-limit.limit:100}")
    private val limit: Long = 100

    @Value("\${quisin.rate-limit.duration:60}")
    private val duration: Long = 60

    @Bean
    fun bucket(): Bucket {
        val refill = Refill.intervally(limit, Duration.ofSeconds(duration))
        val bandwidth = Bandwidth.classic(limit, refill)
        return Bucket.builder().addLimit(bandwidth).build()
    }
} 