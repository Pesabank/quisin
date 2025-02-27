package com.quisin.restaurant.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager().apply {
            setCacheNames(listOf(
                "restaurants",
                "restaurant",
                "chains",
                "chain",
                "restaurantsByOwner",
                "restaurantsByChain",
                "chainsByOwner"
            ))
        }
    }
} 