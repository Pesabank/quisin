package com.quisin.restaurant.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "restaurant_chains")
data class Chain(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @OneToMany(mappedBy = "chain", cascade = [CascadeType.ALL], orphanRemoval = true)
    var restaurants: MutableSet<Restaurant> = mutableSetOf(),

    @Column(nullable = false)
    val ownerId: UUID,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun addRestaurant(restaurant: Restaurant) {
        restaurants.add(restaurant)
        restaurant.chain = this
    }

    fun removeRestaurant(restaurant: Restaurant) {
        restaurants.remove(restaurant)
        restaurant.chain = null
    }
} 