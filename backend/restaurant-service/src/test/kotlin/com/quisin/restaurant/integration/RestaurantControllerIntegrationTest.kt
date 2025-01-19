package com.quisin.restaurant.integration

import com.quisin.restaurant.domain.CuisineType
import com.quisin.restaurant.domain.RestaurantFeature
import com.quisin.restaurant.domain.RestaurantStatus
import com.quisin.restaurant.dto.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.time.DayOfWeek
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestaurantControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val ownerId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        // Setup test data if needed
    }

    @Test
    fun `test create restaurant`() {
        val request = CreateRestaurantRequest(
            name = "Test Restaurant",
            description = "Test Description",
            location = LocationDto(
                address = "Test Address",
                city = "Test City",
                country = "Test Country",
                postalCode = "12345",
                latitude = null,
                longitude = null
            ),
            operatingHours = mapOf(
                DayOfWeek.MONDAY to OperatingHoursDto("09:00", "17:00")
            ),
            features = setOf(RestaurantFeature.DINE_IN),
            cuisineTypes = setOf(CuisineType.ITALIAN),
            chainId = null
        )

        val response = restTemplate.postForEntity(
            "/api/v1/restaurants",
            request,
            RestaurantDto::class.java
        )

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.name == request.name)
        assert(response.body?.description == request.description)
    }

    @Test
    fun `test get restaurant`() {
        val restaurantId = UUID.randomUUID()
        val response = restTemplate.getForEntity(
            "/api/v1/restaurants/$restaurantId",
            RestaurantDto::class.java
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.id == restaurantId)
    }

    @Test
    fun `test update restaurant`() {
        val restaurantId = UUID.randomUUID()
        val request = UpdateRestaurantRequest(
            name = "Updated Restaurant",
            description = "Updated Description",
            location = LocationDto(
                address = "Updated Address",
                city = "Updated City",
                country = "Updated Country",
                postalCode = "54321",
                latitude = null,
                longitude = null
            ),
            operatingHours = mapOf(
                DayOfWeek.TUESDAY to OperatingHoursDto("10:00", "18:00")
            ),
            features = setOf(RestaurantFeature.DELIVERY),
            cuisineTypes = setOf(CuisineType.JAPANESE),
            status = RestaurantStatus.ACTIVE
        )

        val response = restTemplate.exchange(
            "/api/v1/restaurants/$restaurantId",
            HttpMethod.PUT,
            HttpEntity(request),
            RestaurantDto::class.java
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.name == request.name)
        assert(response.body?.description == request.description)
    }

    @Test
    fun `test delete restaurant`() {
        val restaurantId = UUID.randomUUID()
        restTemplate.delete("/api/v1/restaurants/$restaurantId")
    }

    @Test
    fun `test search restaurants`() {
        val request = SearchRestaurantRequest(
            name = "Test",
            city = "Test City",
            cuisineTypes = setOf(CuisineType.ITALIAN),
            features = setOf(RestaurantFeature.DINE_IN),
            status = RestaurantStatus.ACTIVE
        )

        val response = restTemplate.postForEntity(
            "/api/v1/restaurants/search",
            request,
            RestaurantPageResponse::class.java
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body != null)
    }
} 