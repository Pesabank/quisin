package com.quisin.menu.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

enum class MenuItemCategory {
    APPETIZER,
    MAIN_COURSE,
    DESSERT,
    BEVERAGE,
    SPECIAL
}

enum class MenuItemStatus {
    AVAILABLE,
    OUT_OF_STOCK,
    DISCONTINUED
}

@Entity
@Table(name = "menu_items")
@EntityListeners(AuditingEntityListener::class)
data class MenuItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(length = 1000)
    val description: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: MenuItemCategory,

    @Column(nullable = false)
    val imageUrl: String? = null,

    @Column(nullable = false)
    var currentStock: Int = 0,

    @Column(nullable = false)
    var maxStock: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MenuItemStatus = MenuItemStatus.AVAILABLE,

    @Column
    val preparationTime: Int? = null, // in minutes

    @Column
    val calories: Int? = null,

    @ElementCollection
    @CollectionTable(name = "menu_item_allergens", joinColumns = [JoinColumn(name = "menu_item_id")])
    @Column(name = "allergen")
    val allergens: List<String> = listOf(),

    @ElementCollection
    @CollectionTable(name = "menu_item_tags", joinColumns = [JoinColumn(name = "menu_item_id")])
    @Column(name = "tag")
    val tags: List<String> = listOf(),

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null
)
