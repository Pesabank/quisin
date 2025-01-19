package com.quisin.menu.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
class NotificationConfig {

    @Value("\${quisin.services.notification.url}")
    private lateinit var notificationServiceUrl: String

    @Bean
    fun notificationRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .uriTemplateHandler(DefaultUriBuilderFactory(notificationServiceUrl))
            .build()
    }
} 