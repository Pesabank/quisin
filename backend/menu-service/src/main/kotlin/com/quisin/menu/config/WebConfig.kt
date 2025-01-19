package com.quisin.menu.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val rateLimitInterceptor: RateLimitInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/api/v1/menus/**")
            .excludePathPatterns(
                "/api/v1/actuator/**",
                "/api/v1/swagger-ui/**",
                "/api/v1/v3/api-docs/**"
            )
    }
} 