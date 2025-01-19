package com.quisin.restaurant.domain

import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "restaurants")
data class Restaurant(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Embedded
    var location: Location,

    @ElementCollection
    @CollectionTable(name = "restaurant_operating_hours")
    var operatingHours: Map<DayOfWeek, OperatingHours>,

    @ElementCollection
    @CollectionTable(name = "restaurant_features")
    @Enumerated(EnumType.STRING)
    var features: MutableSet<RestaurantFeature>,

    @ElementCollection
    @CollectionTable(name = "restaurant_cuisine_types")
    @Enumerated(EnumType.STRING)
    var cuisineTypes: MutableSet<CuisineType>,

    @Enumerated(EnumType.STRING)
    var status: RestaurantStatus = RestaurantStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chain_id")
    var chain: Chain? = null,

    @Column(nullable = false)
    val ownerId: UUID,

    @ElementCollection
    @CollectionTable(name = "restaurant_images")
    @Column(name = "image_path")
    var imagePaths: MutableSet<String> = mutableSetOf(),

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

@Embeddable
data class Location(
    val address: String,
    val city: String,
    val country: String,
    val postalCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Embeddable
data class OperatingHours(
    val open: String,  // HH:mm format
    val close: String  // HH:mm format
)

enum class RestaurantFeature {
    DINE_IN,
    TAKEAWAY,
    DELIVERY,
    RESERVATIONS,
    OUTDOOR_SEATING,
    WHEELCHAIR_ACCESSIBLE,
    PARKING,
    WIFI,
    LIVE_MUSIC
}

enum class CuisineType {
    ITALIAN,
    CHINESE,
    JAPANESE,
    INDIAN,
    MEXICAN,
    MEDITERRANEAN,
    AMERICAN,
    FRENCH,
    THAI,
    VIETNAMESE,
    KOREAN,
    FUSION
}
