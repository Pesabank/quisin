package com.quisin.restaurant.performance

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import java.time.Duration

class RestaurantSimulation : Simulation() {

    private val httpProtocol = HttpDsl.http
        .baseUrl("http://localhost:8083")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")

    private val createRestaurantRequest = CoreDsl.StringBody("""
        {
            "name": "Test Restaurant",
            "description": "Test Description",
            "location": {
                "address": "Test Address",
                "city": "Test City",
                "country": "Test Country",
                "postalCode": "12345"
            },
            "operatingHours": {
                "MONDAY": {
                    "open": "09:00",
                    "close": "17:00"
                }
            },
            "features": ["DINE_IN"],
            "cuisineTypes": ["ITALIAN"]
        }
    """.trimIndent())

    private val scn = CoreDsl.scenario("Restaurant API Test")
        .exec(
            HttpDsl.http("Create Restaurant")
                .post("/api/v1/restaurants")
                .body(createRestaurantRequest)
                .check(
                    HttpDsl.status().`is`(201),
                    CoreDsl.jmesPath("$.id").saveAs("restaurantId")
                )
        )
        .pause(1)
        .exec(
            HttpDsl.http("Get Restaurant")
                .get("/api/v1/restaurants/#{restaurantId}")
                .check(
                    HttpDsl.status().`is`(200)
                )
        )
        .pause(1)
        .exec(
            HttpDsl.http("Update Restaurant")
                .put("/api/v1/restaurants/#{restaurantId}")
                .body(CoreDsl.StringBody("""
                    {
                        "name": "Updated Restaurant",
                        "description": "Updated Description"
                    }
                """.trimIndent()))
                .check(
                    HttpDsl.status().`is`(200)
                )
        )

    init {
        setUp(
            scn.injectOpen(
                CoreDsl.nothingFor(Duration.ofSeconds(5)),
                CoreDsl.rampUsers(10).during(Duration.ofSeconds(10)),
                CoreDsl.constantUsersPerSec(1.0).during(Duration.ofMinutes(1))
            )
        ).protocols(httpProtocol)
    }
} 