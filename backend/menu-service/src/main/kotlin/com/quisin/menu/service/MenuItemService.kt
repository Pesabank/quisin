package com.quisin.menu.service

import com.quisin.menu.domain.MenuItem
import com.quisin.menu.domain.MenuItemCategory
import com.quisin.menu.domain.MenuItemStatus
import com.quisin.menu.dto.CreateMenuItemRequest
import com.quisin.menu.dto.MenuItemResponse
import com.quisin.menu.dto.UpdateMenuItemRequest
import com.quisin.menu.repository.MenuItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
class MenuItemService(
    private val menuItemRepository: MenuItemRepository
) {
    @Transactional
    fun createMenuItem(request: CreateMenuItemRequest): MenuItemResponse {
        val menuItem = MenuItem(
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
            tags = request.tags,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedMenuItem = menuItemRepository.save(menuItem)
        return mapToMenuItemResponse(savedMenuItem)
    }

    @Transactional(readOnly = true)
    fun getMenuItemById(menuItemId: UUID): MenuItemResponse {
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { NoSuchElementException("Menu item not found") }
        return mapToMenuItemResponse(menuItem)
    }

    @Transactional(readOnly = true)
    fun getMenuItemsByRestaurant(restaurantId: UUID): List<MenuItemResponse> {
        return menuItemRepository.findByRestaurantId(restaurantId)
            .map { mapToMenuItemResponse(it) }
    }

    @Transactional
    fun updateMenuItem(menuItemId: UUID, request: UpdateMenuItemRequest): MenuItemResponse {
        val existingMenuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { NoSuchElementException("Menu item not found") }

        val updatedMenuItem = existingMenuItem.copy(
            name = request.name ?: existingMenuItem.name,
            description = request.description ?: existingMenuItem.description,
            price = request.price ?: existingMenuItem.price,
            category = request.category ?: existingMenuItem.category,
            imageUrl = request.imageUrl ?: existingMenuItem.imageUrl,
            currentStock = request.currentStock ?: existingMenuItem.currentStock,
            maxStock = request.maxStock ?: existingMenuItem.maxStock,
            status = request.status ?: existingMenuItem.status,
            preparationTime = request.preparationTime ?: existingMenuItem.preparationTime,
            calories = request.calories ?: existingMenuItem.calories,
            allergens = request.allergens ?: existingMenuItem.allergens,
            tags = request.tags ?: existingMenuItem.tags,
            updatedAt = LocalDateTime.now()
        )

        val savedMenuItem = menuItemRepository.save(updatedMenuItem)
        return mapToMenuItemResponse(savedMenuItem)
    }

    @Transactional
    fun deleteMenuItem(menuItemId: UUID) {
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { NoSuchElementException("Menu item not found") }
        
        menuItemRepository.delete(menuItem)
    }

    @Transactional(readOnly = true)
    fun searchMenuItems(
        restaurantId: UUID, 
        category: MenuItemCategory? = null,
        status: MenuItemStatus? = null,
        minPrice: BigDecimal? = null,
        maxPrice: BigDecimal? = null,
        searchTerm: String? = null,
        tag: String? = null
    ): List<MenuItemResponse> {
        return when {
            category != null -> 
                menuItemRepository.findByRestaurantIdAndCategory(restaurantId, category)
            
            status != null -> 
                menuItemRepository.findByRestaurantIdAndStatus(restaurantId, status)
            
            searchTerm != null -> 
                menuItemRepository.findByRestaurantIdAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    restaurantId, searchTerm, searchTerm
                )
            
            minPrice != null && maxPrice != null -> 
                menuItemRepository.findByRestaurantIdAndPriceBetween(restaurantId, minPrice, maxPrice)
            
            tag != null -> 
                menuItemRepository.findByRestaurantIdAndTag(restaurantId, tag)
            
            else -> 
                menuItemRepository.findByRestaurantId(restaurantId)
        }.map { mapToMenuItemResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getLowStockItems(restaurantId: UUID): List<MenuItemResponse> {
        return menuItemRepository.findLowStockItems(restaurantId)
            .map { mapToMenuItemResponse(it) }
    }

    private fun mapToMenuItemResponse(menuItem: MenuItem): MenuItemResponse {
        return MenuItemResponse(
            id = menuItem.id!!,
            restaurantId = menuItem.restaurantId,
            name = menuItem.name,
            description = menuItem.description,
            price = menuItem.price,
            category = menuItem.category,
            imageUrl = menuItem.imageUrl,
            currentStock = menuItem.currentStock,
            maxStock = menuItem.maxStock,
            status = menuItem.status,
            preparationTime = menuItem.preparationTime,
            calories = menuItem.calories,
            allergens = menuItem.allergens,
            tags = menuItem.tags,
            createdAt = menuItem.createdAt ?: LocalDateTime.now(),
            updatedAt = menuItem.updatedAt ?: LocalDateTime.now()
        )
    }
}
