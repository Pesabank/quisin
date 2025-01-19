package com.quisin.restaurant.controller

import com.quisin.restaurant.dto.ChainDto
import com.quisin.restaurant.dto.CreateChainRequest
import com.quisin.restaurant.dto.UpdateChainRequest
import com.quisin.restaurant.service.ChainService
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
import java.util.UUID

@RestController
@RequestMapping("/api/v1/chains")
@Tag(name = "Chain Management", description = "APIs for managing restaurant chains")
@SecurityRequirement(name = "bearer-key")
class ChainController(
    private val chainService: ChainService
) {

    @PostMapping
    @Operation(summary = "Create a new restaurant chain")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun createChain(
        @RequestBody @Valid request: CreateChainRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<ChainDto> {
        val ownerId = UUID.fromString(jwt.subject)
        val chain = chainService.createChain(request, ownerId)
        return ResponseEntity(chain, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get chain details")
    fun getChain(@PathVariable id: UUID): ResponseEntity<ChainDto> {
        val chain = chainService.getChain(id)
        return ResponseEntity.ok(chain)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update chain details")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun updateChain(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UpdateChainRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<ChainDto> {
        val ownerId = UUID.fromString(jwt.subject)
        val chain = chainService.updateChain(id, request, ownerId)
        return ResponseEntity.ok(chain)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a chain")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    fun deleteChain(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Unit> {
        val ownerId = UUID.fromString(jwt.subject)
        chainService.deleteChain(id, ownerId)
        return ResponseEntity.noContent().build()
    }
} 