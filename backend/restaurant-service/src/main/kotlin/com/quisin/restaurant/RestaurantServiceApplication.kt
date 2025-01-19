package com.quisin.restaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class RestaurantServiceApplication

fun main(args: Array<String>) {
    runApplication<RestaurantServiceApplication>(*args)
}
