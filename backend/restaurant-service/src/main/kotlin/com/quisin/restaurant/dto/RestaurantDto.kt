package com.quisin.restaurant.dto

import com.quisin.restaurant.domain.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.web.multipart.MultipartFile
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.UUID

data class RestaurantDto(
    val id: UUID?,
    val name: String,
    val description: String,
    val location: LocationDto,
    val operatingHours: Map<DayOfWeek, OperatingHoursDto>,
    val features: Set<RestaurantFeature>,
    val cuisineTypes: Set<CuisineType>,
    val status: RestaurantStatus,
    val chainId: UUID?,
    val ownerId: UUID,
    val images: Set<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class LocationDto(
    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "City is required")
    val city: String,

    @field:NotBlank(message = "Country is required")
    val country: String,

    @field:NotBlank(message = "Postal code is required")
    val postalCode: String,

    @field:DecimalMin(value = "-90.0") @field:DecimalMax(value = "90.0")
    val latitude: Double?,

    @field:DecimalMin(value = "-180.0") @field:DecimalMax(value = "180.0")
    val longitude: Double?
)

data class OperatingHoursDto(
    @field:Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Open time must be in HH:mm format")
    val open: String,

    @field:Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Close time must be in HH:mm format")
    val close: String
)

data class CreateRestaurantRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String,

    @field:Valid
    @field:NotNull(message = "Location is required")
    val location: LocationDto,

    @field:NotEmpty(message = "Operating hours are required")
    val operatingHours: Map<DayOfWeek, @Valid OperatingHoursDto>,

    @field:NotEmpty(message = "At least one feature must be selected")
    val features: Set<RestaurantFeature>,

    @field:NotEmpty(message = "At least one cuisine type must be selected")
    val cuisineTypes: Set<CuisineType>,

    val chainId: UUID?,

    val images: List<MultipartFile>? = null
)

data class UpdateRestaurantRequest(
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String?,

    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String?,

    @field:Valid
    val location: LocationDto?,

    val operatingHours: Map<DayOfWeek, @Valid OperatingHoursDto>?,

    val features: Set<RestaurantFeature>?,

    val cuisineTypes: Set<CuisineType>?,

    val status: RestaurantStatus?
)

data class SearchRestaurantRequest(
    @field:Size(max = 100, message = "Search term must not exceed 100 characters")
    val name: String? = null,

    @field:Size(max = 100, message = "City name must not exceed 100 characters")
    val city: String? = null,

    val cuisineTypes: Set<CuisineType>? = null,
    val features: Set<RestaurantFeature>? = null,
    val status: RestaurantStatus? = null,

    @field:Min(value = 0, message = "Page number cannot be negative")
    val page: Int? = null,

    @field:Min(value = 1, message = "Page size must be at least 1")
    @field:Max(value = 100, message = "Page size must not exceed 100")
    val size: Int? = null
)

data class RestaurantPageResponse(
    val content: List<RestaurantDto>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int
) 