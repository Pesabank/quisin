package com.quisin.analytics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class AnalyticsServiceApplication

fun main(args: Array<String>) {
    runApplication<AnalyticsServiceApplication>(*args)
}
