package com.quisin.menu.controller

import com.quisin.menu.dto.*
import com.quisin.menu.service.MenuService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/menus")
@Tag(name = "Menu Management", description = "APIs for managing restaurant menus")
@SecurityRequirement(name = "bearer-key")
class MenuController(
    private val menuService: MenuService
) {

    @PostMapping
    @Operation(summary = "Create a new menu")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun createMenu(
        @RequestBody @Valid request: CreateMenuRequest
    ): ResponseEntity<MenuDto> {
        val menu = menuService.createMenu(request)
        return ResponseEntity(menu, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu details")
    fun getMenu(@PathVariable id: String): ResponseEntity<MenuDto> {
        val menu = menuService.getMenu(id)
        return ResponseEntity.ok(menu)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update menu details")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun updateMenu(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateMenuRequest
    ): ResponseEntity<MenuDto> {
        val menu = menuService.updateMenu(id, request)
        return ResponseEntity.ok(menu)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun deleteMenu(@PathVariable id: String): ResponseEntity<Unit> {
        menuService.deleteMenu(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/search")
    @Operation(summary = "Search menus with filters")
    fun searchMenus(
        @RequestBody @Valid request: MenuSearchRequest
    ): ResponseEntity<MenuPageResponse> {
        val response = menuService.searchMenus(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get all menus for a restaurant")
    fun getMenusByRestaurant(
        @PathVariable restaurantId: String,
        @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<MenuPageResponse> {
        val request = MenuSearchRequest(
            restaurantId = restaurantId,
            available = if (activeOnly) true else null
        )
        val response = menuService.searchMenus(request)
        return ResponseEntity.ok(response)
    }
} 