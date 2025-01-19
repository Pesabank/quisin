package com.quisin.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    init {
        println("DEBUG: SecurityConfig initialized")
    }
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        println("DEBUG: Configuring security filter chain")
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                println("DEBUG: Configuring authorization rules")
                auth.anyRequest().permitAll()
                println("DEBUG: All requests permitted")
            }
        println("DEBUG: Security filter chain configured")
        return http.build()
    }
}
