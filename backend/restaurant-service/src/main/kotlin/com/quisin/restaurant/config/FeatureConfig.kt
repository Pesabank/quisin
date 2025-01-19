package com.quisin.restaurant.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "quisin.features")
class FeatureConfig {
    var enableChainManagement: Boolean = true
    var enableReservations: Boolean = false
    var enableDelivery: Boolean = false
    var enableAnalytics: Boolean = false
    var enableRealTimeUpdates: Boolean = false
} 