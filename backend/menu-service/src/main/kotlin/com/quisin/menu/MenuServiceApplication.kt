package com.quisin.menu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class MenuServiceApplication

fun main(args: Array<String>) {
    runApplication<MenuServiceApplication>(*args)
}
