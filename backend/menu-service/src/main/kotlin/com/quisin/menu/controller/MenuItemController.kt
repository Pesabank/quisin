package com.quisin.menu.controller

import com.quisin.menu.domain.MenuItemCategory
import com.quisin.menu.domain.MenuItemStatus
import com.quisin.menu.dto.CreateMenuItemRequest
import com.quisin.menu.dto.MenuItemResponse
import com.quisin.menu.dto.UpdateMenuItemRequest
import com.quisin.menu.service.MenuItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/menu-items")
@Tag(name = "Menu Item Management", description = "APIs for managing menu items")
class MenuItemController(
    private val menuItemService: MenuItemService
) {
    @PostMapping
    @Operation(summary = "Create a new menu item", description = "Add a new item to the restaurant's menu")
    fun createMenuItem(
        @Valid @RequestBody request: CreateMenuItemRequest
    ): ResponseEntity<MenuItemResponse> {
        val createdMenuItem = menuItemService.createMenuItem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItem)
    }

    @GetMapping("/{menuItemId}")
    @Operation(summary = "Get menu item by ID", description = "Retrieve details of a specific menu item")
    fun getMenuItemById(
        @PathVariable menuItemId: UUID
    ): ResponseEntity<MenuItemResponse> {
        val menuItem = menuItemService.getMenuItemById(menuItemId)
        return ResponseEntity.ok(menuItem)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get menu items for a restaurant", description = "Retrieve all menu items for a specific restaurant")
    fun getMenuItemsByRestaurant(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<MenuItemResponse>> {
        val menuItems = menuItemService.getMenuItemsByRestaurant(restaurantId)
        return ResponseEntity.ok(menuItems)
    }

    @PutMapping("/{menuItemId}")
    @Operation(summary = "Update a menu item", description = "Update details of an existing menu item")
    fun updateMenuItem(
        @PathVariable menuItemId: UUID,
        @Valid @RequestBody request: UpdateMenuItemRequest
    ): ResponseEntity<MenuItemResponse> {
        val updatedMenuItem = menuItemService.updateMenuItem(menuItemId, request)
        return ResponseEntity.ok(updatedMenuItem)
    }

    @DeleteMapping("/{menuItemId}")
    @Operation(summary = "Delete a menu item", description = "Remove a menu item from the restaurant's menu")
    fun deleteMenuItem(
        @PathVariable menuItemId: UUID
    ): ResponseEntity<Void> {
        menuItemService.deleteMenuItem(menuItemId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search")
    @Operation(summary = "Search menu items", description = "Search menu items with various filters")
    fun searchMenuItems(
        @RequestParam restaurantId: UUID,
        @RequestParam(required = false) category: MenuItemCategory?,
        @RequestParam(required = false) status: MenuItemStatus?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) searchTerm: String?,
        @RequestParam(required = false) tag: String?
    ): ResponseEntity<List<MenuItemResponse>> {
        val menuItems = menuItemService.searchMenuItems(
            restaurantId, category, status, minPrice, maxPrice, searchTerm, tag
        )
        return ResponseEntity.ok(menuItems)
    }

    @GetMapping("/low-stock/{restaurantId}")
    @Operation(summary = "Get low stock menu items", description = "Retrieve menu items with stock below 20% of max stock")
    fun getLowStockItems(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<MenuItemResponse>> {
        val lowStockItems = menuItemService.getLowStockItems(restaurantId)
        return ResponseEntity.ok(lowStockItems)
    }
}
