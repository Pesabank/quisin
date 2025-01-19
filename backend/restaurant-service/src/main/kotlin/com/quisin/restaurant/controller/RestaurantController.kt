package com.quisin.restaurant.controller

import com.quisin.restaurant.dto.*
import com.quisin.restaurant.service.ImageService
import com.quisin.restaurant.service.RestaurantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant Management", description = "APIs for managing restaurants")
@SecurityRequirement(name = "bearer-key")
class RestaurantController(
    private val restaurantService: RestaurantService,
    private val imageService: ImageService
) {

    @PostMapping
    @Operation(
        summary = "Create a new restaurant",
        description = "Creates a new restaurant for the authenticated owner"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Restaurant created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid request data"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    ])
    fun createRestaurant(
        @RequestBody @Valid request: CreateRestaurantRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<RestaurantDto> {
        val ownerId = UUID.fromString(jwt.subject)
        val restaurant = restaurantService.createRestaurant(request, ownerId)
        return ResponseEntity(restaurant, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get restaurant details",
        description = "Retrieves detailed information about a specific restaurant"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Restaurant details retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Restaurant not found")
    ])
    fun getRestaurant(@PathVariable id: UUID): ResponseEntity<RestaurantDto> {
        val restaurant = restaurantService.getRestaurant(id)
        return ResponseEntity.ok(restaurant)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update restaurant details",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun updateRestaurant(
        @PathVariable id: UUID,
        @RequestBody request: UpdateRestaurantRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<RestaurantDto> {
        val ownerId = UUID.fromString(jwt.subject)
        val restaurant = restaurantService.updateRestaurant(id, request, ownerId)
        return ResponseEntity.ok(restaurant)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a restaurant",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun deleteRestaurant(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Unit> {
        val ownerId = UUID.fromString(jwt.subject)
        restaurantService.deleteRestaurant(id, ownerId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/search")
    @Operation(
        summary = "Search restaurants with filters",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    fun searchRestaurants(@RequestBody request: SearchRestaurantRequest): RestaurantPageResponse {
        return restaurantService.searchRestaurants(request)
    }

    @GetMapping("/owner")
    @Operation(
        summary = "Get restaurants by owner ID",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun getRestaurantsByOwner(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<RestaurantResponse>> {
        val ownerId = UUID.fromString(jwt.subject)
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwner(ownerId))
    }

    @GetMapping("/chain/{chainId}")
    @Operation(
        summary = "Get restaurants by chain ID",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    fun getRestaurantsByChain(@PathVariable chainId: UUID): ResponseEntity<List<RestaurantResponse>> {
        return ResponseEntity.ok(restaurantService.getRestaurantsByChain(chainId))
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Upload restaurant images")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun uploadImages(
        @PathVariable id: UUID,
        @RequestParam("images") images: List<MultipartFile>,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<RestaurantDto> {
        val ownerId = UUID.fromString(jwt.subject)
        val restaurant = restaurantService.addImages(id, images, ownerId)
        return ResponseEntity.ok(restaurant)
    }

    @DeleteMapping("/{id}/images/{imageName}")
    @Operation(summary = "Delete restaurant image")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun deleteImage(
        @PathVariable id: UUID,
        @PathVariable imageName: String,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Unit> {
        val ownerId = UUID.fromString(jwt.subject)
        restaurantService.deleteImage(id, imageName, ownerId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/images/{imageName}")
    @Operation(summary = "Get restaurant image")
    fun getImage(@PathVariable imageName: String): ResponseEntity<Resource> {
        val imagePath = imageService.getImagePath(imageName)
        val resource = UrlResource(imagePath.toUri())

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("image/*"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${imagePath.fileName}\"")
            .body(resource)
    }
}
