package com.quisin.restaurant.service

import com.quisin.restaurant.client.UserServiceClient
import com.quisin.restaurant.domain.*
import com.quisin.restaurant.dto.*
import com.quisin.restaurant.event.*
import com.quisin.restaurant.repository.ChainRepository
import com.quisin.restaurant.repository.RestaurantRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.UUID

@Service
class RestaurantService(
    private val restaurantRepository: RestaurantRepository,
    private val chainRepository: ChainRepository,
    private val imageService: ImageService,
    private val asyncImageService: AsyncImageService,
    private val eventPublisher: EventPublisherService,
    private val userServiceClient: UserServiceClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>
) {

    @Transactional
    fun createRestaurant(request: CreateRestaurantRequest, ownerId: UUID): RestaurantDto {
        // Validate owner using circuit breaker
        val userResponse = circuitBreakerFactory.create("getUser").run({
            userServiceClient.getUser(ownerId)
        }, { throwable ->
            throw IllegalStateException("Unable to validate user: ${throwable.message}")
        })

        if (restaurantRepository.existsByNameAndOwnerId(request.name, ownerId)) {
            throw IllegalArgumentException("Restaurant with this name already exists for this owner")
        }

        val chain = request.chainId?.let { chainId ->
            chainRepository.findById(chainId).orElseThrow {
                IllegalArgumentException("Chain not found with id: $chainId")
            }
        }

        val restaurant = Restaurant(
            name = request.name,
            description = request.description,
            location = Location(
                address = request.location.address,
                city = request.location.city,
                country = request.location.country,
                postalCode = request.location.postalCode,
                latitude = request.location.latitude,
                longitude = request.location.longitude
            ),
            operatingHours = request.operatingHours.mapValues { (_, value) ->
                OperatingHours(value.open, value.close)
            },
            features = request.features.toMutableSet(),
            cuisineTypes = request.cuisineTypes.toMutableSet(),
            chain = chain,
            ownerId = ownerId,
            status = RestaurantStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedRestaurant = restaurantRepository.save(restaurant)
        
        // Publish event
        eventPublisher.publishEvent(RestaurantCreatedEvent(
            restaurantId = savedRestaurant.id!!,
            name = savedRestaurant.name,
            ownerId = savedRestaurant.ownerId,
            chainId = savedRestaurant.chain?.id
        ))

        return savedRestaurant.toDto()
    }

    @Transactional
    fun updateRestaurant(id: UUID, request: UpdateRestaurantRequest, ownerId: UUID): RestaurantDto {
        val restaurant = restaurantRepository.findById(id).orElseThrow {
            IllegalArgumentException("Restaurant not found with id: $id")
        }

        if (restaurant.ownerId != ownerId) {
            throw IllegalStateException("Not authorized to update this restaurant")
        }

        val oldStatus = restaurant.status

        request.name?.let { restaurant.name = it }
        request.description?.let { restaurant.description = it }
        request.location?.let {
            restaurant.location = Location(
                address = it.address,
                city = it.city,
                country = it.country,
                postalCode = it.postalCode,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
        request.operatingHours?.let {
            restaurant.operatingHours = it.mapValues { (_, value) ->
                OperatingHours(value.open, value.close)
            }
        }
        request.features?.let { restaurant.features = it.toMutableSet() }
        request.cuisineTypes?.let { restaurant.cuisineTypes = it.toMutableSet() }
        request.status?.let { restaurant.status = it }

        restaurant.updatedAt = LocalDateTime.now()
        val updatedRestaurant = restaurantRepository.save(restaurant)

        // Publish events
        if (request.status != null && oldStatus != restaurant.status) {
            eventPublisher.publishEvent(RestaurantStatusChangedEvent(
                restaurantId = restaurant.id!!,
                oldStatus = oldStatus,
                newStatus = restaurant.status,
                ownerId = restaurant.ownerId
            ))
        }

        eventPublisher.publishEvent(RestaurantUpdatedEvent(
            restaurantId = restaurant.id!!,
            name = request.name,
            description = request.description,
            ownerId = restaurant.ownerId
        ))

        return updatedRestaurant.toDto()
    }

    @Transactional
    fun deleteRestaurant(id: UUID, ownerId: UUID) {
        val restaurant = restaurantRepository.findById(id).orElseThrow {
            IllegalArgumentException("Restaurant not found with id: $id")
        }

        if (restaurant.ownerId != ownerId) {
            throw IllegalStateException("Not authorized to delete this restaurant")
        }

        // Delete all images
        restaurant.imagePaths.forEach { imagePath ->
            imageService.deleteImage(imagePath)
        }

        restaurantRepository.delete(restaurant)

        // Publish event
        eventPublisher.publishEvent(RestaurantDeletedEvent(
            restaurantId = restaurant.id!!,
            ownerId = restaurant.ownerId,
            chainId = restaurant.chain?.id
        ))
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = ["restaurant"], key = "#id")
    fun getRestaurant(id: UUID): RestaurantDto {
        val restaurant = restaurantRepository.findById(id).orElseThrow {
            IllegalArgumentException("Restaurant not found with id: $id")
        }
        return restaurant.toDto()
    }

    @Transactional(readOnly = true)
    fun searchRestaurants(request: SearchRestaurantRequest): RestaurantPageResponse {
        val specification = RestaurantSpecification.createSpecification(request)
        val pageable = PageRequest.of(
            request.page ?: 0,
            request.size ?: 20,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val restaurantsPage = restaurantRepository.findAll(specification, pageable)
        
        return RestaurantPageResponse(
            content = restaurantsPage.content.map { it.toDto() },
            totalElements = restaurantsPage.totalElements,
            totalPages = restaurantsPage.totalPages,
            currentPage = restaurantsPage.number
        )
    }

    @Transactional(readOnly = true)
    fun getRestaurantsByOwner(ownerId: UUID): List<RestaurantResponse> {
        return restaurantRepository.findByOwnerId(ownerId)
            .map { restaurant ->
                RestaurantResponse(
                    id = restaurant.id!!,
                    name = restaurant.name,
                    address = restaurant.location.address,
                    contactNumber = "",  // This should come from user service
                    email = "",          // This should come from user service
                    status = restaurant.status,
                    adminUserId = restaurant.ownerId,
                    description = restaurant.description,
                    websiteUrl = null,
                    createdAt = java.util.Date.from(restaurant.createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant()),
                    updatedAt = java.util.Date.from(restaurant.updatedAt.atZone(java.time.ZoneId.systemDefault()).toInstant()),
                    chainCount = if (restaurant.chain != null) 1 else 0
                )
            }
    }

    @Transactional(readOnly = true)
    fun getRestaurantsByChain(chainId: UUID): List<RestaurantResponse> {
        return restaurantRepository.findByChainId(chainId)
            .map { restaurant ->
                RestaurantResponse(
                    id = restaurant.id!!,
                    name = restaurant.name,
                    address = restaurant.location.address,
                    contactNumber = "",  // This should come from user service
                    email = "",          // This should come from user service
                    status = restaurant.status,
                    adminUserId = restaurant.ownerId,
                    description = restaurant.description,
                    websiteUrl = null,
                    createdAt = java.util.Date.from(restaurant.createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant()),
                    updatedAt = java.util.Date.from(restaurant.updatedAt.atZone(java.time.ZoneId.systemDefault()).toInstant()),
                    chainCount = if (restaurant.chain != null) 1 else 0
                )
            }
    }

    @Transactional
    @CacheEvict(cacheNames = ["restaurants", "restaurant"], key = "#id")
    fun addImages(id: UUID, images: List<MultipartFile>, ownerId: UUID): RestaurantDto {
        val restaurant = restaurantRepository.findById(id).orElseThrow {
            IllegalArgumentException("Restaurant not found with id: $id")
        }

        if (restaurant.ownerId != ownerId) {
            throw IllegalStateException("Not authorized to update this restaurant")
        }

        // Process images asynchronously
        images.forEach { image ->
            asyncImageService.processAndSaveImage(image, id)
                .also { imagePath -> restaurant.imagePaths.add(imagePath) }
        }

        restaurant.updatedAt = LocalDateTime.now()
        return restaurantRepository.save(restaurant).toDto()
    }

    @Transactional
    @CacheEvict(cacheNames = ["restaurants", "restaurant"], key = "#id")
    fun deleteImage(id: UUID, imageName: String, ownerId: UUID) {
        val restaurant = restaurantRepository.findById(id).orElseThrow {
            IllegalArgumentException("Restaurant not found with id: $id")
        }

        if (restaurant.ownerId != ownerId) {
            throw IllegalStateException("Not authorized to update this restaurant")
        }

        if (!restaurant.imagePaths.remove(imageName)) {
            throw IllegalArgumentException("Image not found: $imageName")
        }

        imageService.deleteImage(imageName)
        restaurant.updatedAt = LocalDateTime.now()
        restaurantRepository.save(restaurant)
    }

    private fun Restaurant.toDto() = RestaurantDto(
        id = id,
        name = name,
        description = description,
        location = LocationDto(
            address = location.address,
            city = location.city,
            country = location.country,
            postalCode = location.postalCode,
            latitude = location.latitude,
            longitude = location.longitude
        ),
        operatingHours = operatingHours.mapValues { (_, value) ->
            OperatingHoursDto(value.open, value.close)
        },
        features = features,
        cuisineTypes = cuisineTypes,
        status = status,
        chainId = chain?.id,
        ownerId = ownerId,
        images = imagePaths,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

class RestaurantSpecification {
    companion object {
        fun createSpecification(request: SearchRestaurantRequest): Specification<Restaurant> {
            return Specification { root, query, criteriaBuilder ->
                val predicates = mutableListOf<Predicate>()

                request.name?.let {
                    predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%${it.lowercase()}%"
                        )
                    )
                }

                request.city?.let {
                    predicates.add(
                        criteriaBuilder.equal(
                            root.get<Location>("location").get<String>("city"),
                            it
                        )
                    )
                }

                request.cuisineTypes?.let { types ->
                    predicates.add(root.join<Restaurant, CuisineType>("cuisineTypes").`in`(types))
                }

                request.features?.let { feats ->
                    predicates.add(root.join<Restaurant, RestaurantFeature>("features").`in`(feats))
                }

                request.status?.let {
                    predicates.add(criteriaBuilder.equal(root.get<RestaurantStatus>("status"), it))
                }

                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
    }
}
