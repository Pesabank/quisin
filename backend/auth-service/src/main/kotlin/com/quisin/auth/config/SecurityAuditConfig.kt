package com.quisin.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
@EnableJpaAuditing
class SecurityAuditConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAware {
            Optional.ofNullable(SecurityContextHolder.getContext().authentication)
                .filter { it.isAuthenticated }
                .map { it.name }
                .or { Optional.of("SYSTEM") }
        }
    }
}
