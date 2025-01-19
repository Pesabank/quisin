package com.quisin.restaurant.service

import com.quisin.restaurant.client.UserResponse
import com.quisin.restaurant.client.UserServiceClient
import com.quisin.restaurant.domain.*
import com.quisin.restaurant.dto.*
import com.quisin.restaurant.event.RestaurantCreatedEvent
import com.quisin.restaurant.event.RestaurantUpdatedEvent
import com.quisin.restaurant.repository.ChainRepository
import com.quisin.restaurant.repository.RestaurantRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.UUID

class RestaurantServiceTest {
    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var chainRepository: ChainRepository
    private lateinit var userServiceClient: UserServiceClient
    private lateinit var restaurantService: RestaurantService
    private lateinit var imageService: ImageService
    private lateinit var asyncImageService: AsyncImageService
    private lateinit var eventPublisherService: EventPublisherService
    private lateinit var circuitBreakerFactory: CircuitBreakerFactory<*, *>
    private lateinit var circuitBreaker: CircuitBreaker

    @BeforeEach
    fun setup() {
        restaurantRepository = mockk()
        chainRepository = mockk()
        userServiceClient = mockk()
        imageService = mockk()
        asyncImageService = mockk()
        eventPublisherService = mockk()
        circuitBreakerFactory = mockk()
        circuitBreaker = mockk()

        every { circuitBreakerFactory.create(any()) } returns circuitBreaker
        every { circuitBreaker.run(any<() -> UserResponse>(), any()) } answers {
            firstArg<() -> UserResponse>().invoke()
        }

        restaurantService = RestaurantService(
            restaurantRepository,
            chainRepository,
            imageService,
            asyncImageService,
            eventPublisherService,
            userServiceClient,
            circuitBreakerFactory
        )
    }

    @Test
    fun `test create restaurant`() {
        val ownerId = UUID.randomUUID()
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

        val userResponse = UserResponse(
            id = ownerId,
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            roles = setOf("ROLE_RESTAURANT_OWNER"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { userServiceClient.getUser(ownerId) } returns userResponse
        every { restaurantRepository.existsByNameAndOwnerId(any(), any()) } returns false
        every { restaurantRepository.save(any()) } answers {
            firstArg<Restaurant>().copy(id = UUID.randomUUID())
        }
        every { eventPublisherService.publishEvent(any<RestaurantCreatedEvent>()) } just Runs

        val result = restaurantService.createRestaurant(request, ownerId)

        assertNotNull(result.id)
        assertEquals(request.name, result.name)
        assertEquals(request.description, result.description)
        assertEquals(request.location.address, result.location.address)
        assertEquals(request.features, result.features)
        assertEquals(request.cuisineTypes, result.cuisineTypes)
        assertEquals(RestaurantStatus.PENDING, result.status)

        verify(exactly = 1) { restaurantRepository.save(any()) }
        verify(exactly = 1) { eventPublisherService.publishEvent(any<RestaurantCreatedEvent>()) }
    }

    @Test
    fun `test update restaurant`() {
        val ownerId = UUID.randomUUID()
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

        val existingRestaurant = Restaurant(
            id = restaurantId,
            name = "Old Name",
            description = "Old Description",
            location = Location(
                address = "Old Address",
                city = "Old City",
                country = "Old Country",
                postalCode = "12345"
            ),
            operatingHours = mapOf(
                DayOfWeek.MONDAY to OperatingHours("09:00", "17:00")
            ),
            features = mutableSetOf(RestaurantFeature.DINE_IN),
            cuisineTypes = mutableSetOf(CuisineType.ITALIAN),
            status = RestaurantStatus.PENDING,
            ownerId = ownerId
        )

        every { restaurantRepository.findById(restaurantId) } returns java.util.Optional.of(existingRestaurant)
        every { restaurantRepository.save(any()) } answers { firstArg() }
        every { eventPublisherService.publishEvent(any<RestaurantUpdatedEvent>()) } just Runs

        val result = restaurantService.updateRestaurant(restaurantId, request, ownerId)

        assertEquals(request.name, result.name)
        assertEquals(request.description, result.description)
        assertEquals(request.location?.address, result.location.address)
        assertEquals(request.features, result.features)
        assertEquals(request.cuisineTypes, result.cuisineTypes)
        assertEquals(request.status, result.status)

        verify(exactly = 1) { restaurantRepository.save(any()) }
        verify(exactly = 1) { eventPublisherService.publishEvent(any<RestaurantUpdatedEvent>()) }
    }

    @Test
    fun `test get restaurant`() {
        val restaurantId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val restaurant = Restaurant(
            id = restaurantId,
            name = "Test Restaurant",
            description = "Test Description",
            location = Location(
                address = "Test Address",
                city = "Test City",
                country = "Test Country",
                postalCode = "12345"
            ),
            operatingHours = mapOf(
                DayOfWeek.MONDAY to OperatingHours("09:00", "17:00")
            ),
            features = mutableSetOf(RestaurantFeature.DINE_IN),
            cuisineTypes = mutableSetOf(CuisineType.ITALIAN),
            status = RestaurantStatus.ACTIVE,
            ownerId = ownerId
        )

        every { restaurantRepository.findById(restaurantId) } returns java.util.Optional.of(restaurant)

        val result = restaurantService.getRestaurant(restaurantId)

        assertEquals(restaurant.id, result.id)
        assertEquals(restaurant.name, result.name)
        assertEquals(restaurant.description, result.description)
        assertEquals(restaurant.location.address, result.location.address)
        assertEquals(restaurant.features, result.features)
        assertEquals(restaurant.cuisineTypes, result.cuisineTypes)
        assertEquals(restaurant.status, result.status)

        verify(exactly = 1) { restaurantRepository.findById(restaurantId) }
    }
} 