package com.quisin.menu.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "menus")
data class Menu(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    val restaurantId: String,

    @OneToMany(mappedBy = "menu", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableSet<MenuCategory> = mutableSetOf(),

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "menu_categories")
data class MenuCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    var menu: Menu,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableSet<MenuItem> = mutableSetOf(),

    @Column(nullable = false)
    var displayOrder: Int,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

@Embeddable
data class MenuItemOption(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val price: BigDecimal,

    @Column(nullable = false)
    val available: Boolean = true
)

enum class Allergen {
    GLUTEN,
    CRUSTACEANS,
    EGGS,
    FISH,
    PEANUTS,
    SOYBEANS,
    MILK,
    NUTS,
    CELERY,
    MUSTARD,
    SESAME,
    SULPHITES,
    LUPIN,
    MOLLUSCS
}

enum class DietaryInfo {
    VEGETARIAN,
    VEGAN,
    HALAL,
    KOSHER,
    GLUTEN_FREE,
    DAIRY_FREE,
    LOW_CALORIE,
    KETO,
    PALEO
} 