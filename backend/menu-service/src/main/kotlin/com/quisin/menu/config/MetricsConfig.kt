package com.quisin.menu.config

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class MetricsConfig {

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                listOf(
                    Tag.of("application", "menu-service"),
                    Tag.of("env", System.getenv("SPRING_PROFILES_ACTIVE") ?: "default")
                )
            )
        }
    }

    @Bean
    fun timedAspect(registry: MeterRegistry): TimedAspect {
        return TimedAspect(registry)
    }

    companion object {
        // Counter names
        const val MENU_CREATED_COUNTER = "menu.created.count"
        const val MENU_UPDATED_COUNTER = "menu.updated.count"
        const val MENU_DELETED_COUNTER = "menu.deleted.count"
        const val MENU_SEARCH_COUNTER = "menu.search.count"
        
        // Timer names
        const val MENU_CREATION_TIMER = "menu.creation.time"
        const val MENU_UPDATE_TIMER = "menu.update.time"
        const val MENU_SEARCH_TIMER = "menu.search.time"
        
        // Gauge names
        const val ACTIVE_MENUS_GAUGE = "menu.active.count"
        const val MENU_ITEMS_GAUGE = "menu.items.count"
    }
} 