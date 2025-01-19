package com.quisin.restaurant.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Value("\${quisin.cors.allowed-origins:*}")
    private val allowedOrigins: String,
    
    @Value("\${quisin.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private val allowedMethods: String,
    
    @Value("\${quisin.cors.allowed-headers:*}")
    private val allowedHeaders: String,
    
    @Value("\${quisin.cors.max-age:3600}")
    private val maxAge: Long
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins.split(",").toTypedArray())
            .allowedMethods(*allowedMethods.split(",").toTypedArray())
            .allowedHeaders(*allowedHeaders.split(",").toTypedArray())
            .allowCredentials(true)
            .maxAge(maxAge)
    }
} 