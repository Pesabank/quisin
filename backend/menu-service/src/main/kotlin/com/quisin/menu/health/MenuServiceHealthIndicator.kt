package com.quisin.menu.health

import com.quisin.menu.repository.MenuRepository
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class MenuServiceHealthIndicator(
    private val menuRepository: MenuRepository
) : HealthIndicator {

    override fun health(): Health {
        return try {
            val menuCount = menuRepository.count()
            val activeMenuCount = menuRepository.countByActive(true)
            
            Health.up()
                .withDetail("totalMenus", menuCount)
                .withDetail("activeMenus", activeMenuCount)
                .withDetail("status", "Menu service is operational")
                .build()
        } catch (e: Exception) {
            Health.down()
                .withException(e)
                .withDetail("status", "Menu service is not operational")
                .build()
        }
    }
} 