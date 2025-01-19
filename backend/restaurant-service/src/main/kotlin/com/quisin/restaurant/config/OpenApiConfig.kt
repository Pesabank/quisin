package com.quisin.restaurant.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Restaurant Service API",
        version = "1.0",
        description = "API for managing restaurants and restaurant chains",
        contact = Contact(
            name = "Quisin Support",
            email = "support@quisin.com",
            url = "https://quisin.com/support"
        )
    ),
    servers = [
        Server(url = "/", description = "Default Server URL")
    ],
    security = [
        SecurityRequirement(name = "bearer-key")
    ]
)
@SecurityScheme(
    name = "bearer-key",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class OpenApiConfig 