package com.quisin.auth.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Quisin Authentication Service",
        version = "1.0.0",
        description = "Authentication and Authorization Service for Quisin Restaurant Management System",
        contact = Contact(
            name = "Quisin Support",
            email = "support@quisin.com"
        )
    ),
    servers = [
        Server(url = "http://localhost:8081", description = "Local Development Server")
    ]
)
class SwaggerConfig {

    @Bean
    fun authenticationApi(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            openApi.paths.values.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    operation.tags = listOf("Authentication")
                }
            }
        }
    }
}
