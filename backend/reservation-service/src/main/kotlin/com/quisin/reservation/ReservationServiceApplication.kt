package com.quisin.reservation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class ReservationServiceApplication

fun main(args: Array<String>) {
    runApplication<ReservationServiceApplication>(*args)
}
