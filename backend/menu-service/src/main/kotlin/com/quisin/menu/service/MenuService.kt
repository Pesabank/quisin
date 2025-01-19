package com.quisin.menu.service

import com.quisin.menu.client.RestaurantServiceClient
import com.quisin.menu.domain.*
import com.quisin.menu.dto.*
import com.quisin.menu.event.*
import com.quisin.menu.repository.MenuRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class MenuService(
    private val menuRepository: MenuRepository,
    private val restaurantServiceClient: RestaurantServiceClient,
    private val eventPublisher: EventPublisherService,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>
) {

    @Transactional
    fun createMenu(request: CreateMenuRequest): MenuDto {
        // Validate restaurant using circuit breaker
        val restaurantResponse = circuitBreakerFactory.create("getRestaurant").run({
            restaurantServiceClient.getRestaurant(request.restaurantId)
        }, { throwable ->
            throw IllegalStateException("Unable to validate restaurant: ${throwable.message}")
        })

        val newMenu = Menu(
            name = request.name,
            description = request.description,
            restaurantId = request.restaurantId
        )

        val savedMenu = menuRepository.save(newMenu)

        // Add categories
        request.categories.forEachIndexed { index, categoryRequest ->
            val category = createMenuCategory(categoryRequest, savedMenu)
            savedMenu.categories.add(category)
        }

        val finalMenu = menuRepository.save(savedMenu)

        // Publish event
        eventPublisher.publishEvent(MenuCreatedEvent(
            menuId = finalMenu.id!!,
            name = finalMenu.name,
            restaurantId = finalMenu.restaurantId
        ))

        return finalMenu.toDto()
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = ["menu"], key = "#id")
    fun getMenu(id: String): MenuDto {
        val menu = menuRepository.findById(id).orElseThrow {
            IllegalArgumentException("Menu not found with id: $id")
        }
        return menu.toDto()
    }

    @Transactional
    @CacheEvict(cacheNames = ["menu", "menus"], key = "#id")
    fun updateMenu(id: String, request: UpdateMenuRequest): MenuDto {
        val menu = menuRepository.findById(id).orElseThrow {
            IllegalArgumentException("Menu not found with id: $id")
        }

        request.name?.let { menu.name = it }
        request.description?.let { menu.description = it }
        request.active?.let { menu.active = it }

        menu.updatedAt = LocalDateTime.now()
        val updatedMenu = menuRepository.save(menu)

        // Publish event
        eventPublisher.publishEvent(MenuUpdatedEvent(
            menuId = menu.id!!,
            name = request.name,
            description = request.description,
            active = request.active
        ))

        return updatedMenu.toDto()
    }

    @Transactional
    @CacheEvict(cacheNames = ["menu", "menus"], key = "#id")
    fun deleteMenu(id: String) {
        val menu = menuRepository.findById(id).orElseThrow {
            IllegalArgumentException("Menu not found with id: $id")
        }

        menuRepository.delete(menu)

        // Publish event
        eventPublisher.publishEvent(MenuDeletedEvent(
            menuId = menu.id!!,
            restaurantId = menu.restaurantId
        ))
    }

    @Transactional(readOnly = true)
    fun searchMenus(request: MenuSearchRequest): MenuPageResponse {
        val specification = createMenuSpecification(request)
        val pageable = PageRequest.of(
            request.page ?: 0,
            request.size ?: 20,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val menuPage = menuRepository.findAll(specification, pageable)

        return MenuPageResponse(
            content = menuPage.content.map { it.toDto() },
            totalElements = menuPage.totalElements,
            totalPages = menuPage.totalPages,
            currentPage = menuPage.number
        )
    }

    private fun createMenuCategory(request: CreateMenuCategoryRequest, menu: Menu): MenuCategory {
        return MenuCategory(
            name = request.name,
            description = request.description,
            menu = menu,
            displayOrder = request.displayOrder,
            items = request.items.mapIndexed { index, itemRequest ->
                createMenuItem(itemRequest)
            }.toMutableSet()
        )
    }

    private fun createMenuItem(request: CreateMenuItemRequest): MenuItem {
        return MenuItem(
            restaurantId = request.restaurantId,
            name = request.name,
            description = request.description,
            price = request.price,
            category = request.category,
            imageUrl = request.imageUrl,
            currentStock = request.currentStock,
            maxStock = request.maxStock,
            status = request.status,
            preparationTime = request.preparationTime,
            calories = request.calories,
            allergens = request.allergens,
            tags = request.tags
        )
    }

    private fun createMenuSpecification(request: MenuSearchRequest): Specification<Menu> {
        return Specification { root, query, criteriaBuilder ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            request.name?.let {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%${it.lowercase()}%"
                    )
                )
            }

            request.restaurantId?.let {
                predicates.add(
                    criteriaBuilder.equal(root.get<String>("restaurantId"), it)
                )
            }

            request.available?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("active"), it))
            }

            if (request.allergens?.isNotEmpty() == true) {
                val itemJoin = root.join<Menu, MenuCategory>("categories")
                    .join<MenuCategory, MenuItem>("items")
                predicates.add(itemJoin.join<MenuItem, Allergen>("allergens").`in`(request.allergens))
            }

            if (request.dietaryInfo?.isNotEmpty() == true) {
                val itemJoin = root.join<Menu, MenuCategory>("categories")
                    .join<MenuCategory, MenuItem>("items")
                predicates.add(itemJoin.join<MenuItem, DietaryInfo>("dietaryInfo").`in`(request.dietaryInfo))
            }

            request.priceRange?.let {
                val itemJoin = root.join<Menu, MenuCategory>("categories")
                    .join<MenuCategory, MenuItem>("items")
                predicates.add(
                    criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(itemJoin.get("price"), it.min),
                        criteriaBuilder.lessThanOrEqualTo(itemJoin.get("price"), it.max)
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun Menu.toDto() = MenuDto(
        id = id,
        name = name,
        description = description,
        restaurantId = restaurantId,
        categories = categories.sortedBy { it.displayOrder }.map { category ->
            MenuCategoryDto(
                id = category.id,
                name = category.name,
                description = category.description,
                items = category.items.map { item ->
                    MenuItemDto(
                        id = item.id,
                        name = item.name,
                        description = item.description,
                        price = item.price,
                        category = item.category,
                        imageUrl = item.imageUrl,
                        currentStock = item.currentStock,
                        maxStock = item.maxStock,
                        status = item.status,
                        preparationTime = item.preparationTime,
                        calories = item.calories,
                        allergens = item.allergens,
                        tags = item.tags,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt
                    )
                },
                displayOrder = category.displayOrder,
                active = category.active,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt
            )
        },
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 