package com.quisin.menu.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitConfig {

    @Bean
    fun rateLimitBucket(): Bucket {
        val requestLimit = 100L // requests
        val duration = Duration.ofMinutes(1)
        
        val refill = Refill.intervally(requestLimit, duration)
        val bandwidth = Bandwidth.classic(requestLimit, refill)
        
        return Bucket.builder()
            .addLimit(bandwidth)
            .build()
    }
} 