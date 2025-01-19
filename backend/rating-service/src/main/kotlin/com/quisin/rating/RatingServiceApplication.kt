package com.quisin.rating

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class RatingServiceApplication

fun main(args: Array<String>) {
    runApplication<RatingServiceApplication>(*args)
}
