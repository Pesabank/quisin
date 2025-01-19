package com.quisin.restaurant.service

import com.quisin.restaurant.domain.Chain
import com.quisin.restaurant.dto.ChainDto
import com.quisin.restaurant.dto.CreateChainRequest
import com.quisin.restaurant.dto.UpdateChainRequest
import com.quisin.restaurant.event.ChainCreatedEvent
import com.quisin.restaurant.event.ChainDeletedEvent
import com.quisin.restaurant.event.ChainUpdatedEvent
import com.quisin.restaurant.repository.ChainRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ChainService(
    private val chainRepository: ChainRepository,
    private val eventPublisherService: EventPublisherService
) {

    @Transactional
    fun createChain(request: CreateChainRequest, ownerId: UUID): ChainDto {
        if (chainRepository.existsByNameAndOwnerId(request.name, ownerId)) {
            throw IllegalArgumentException("Chain with this name already exists for this owner")
        }

        val chain = Chain(
            name = request.name,
            description = request.description,
            ownerId = ownerId
        )

        val savedChain = chainRepository.save(chain)
        
        eventPublisherService.publishEvent(
            ChainCreatedEvent(
                chainId = savedChain.id!!,
                ownerId = ownerId,
                name = savedChain.name
            )
        )

        return savedChain.toDto()
    }

    @Transactional(readOnly = true)
    fun getChain(id: UUID): ChainDto {
        return chainRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Chain not found with id: $id") }
            .toDto()
    }

    @Transactional
    fun updateChain(id: UUID, request: UpdateChainRequest, ownerId: UUID): ChainDto {
        val chain = chainRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Chain not found with id: $id") }

        if (chain.ownerId != ownerId) {
            throw IllegalArgumentException("You don't have permission to update this chain")
        }

        chain.name = request.name
        chain.description = request.description
        chain.updatedAt = LocalDateTime.now()

        val updatedChain = chainRepository.save(chain)
        
        eventPublisherService.publishEvent(
            ChainUpdatedEvent(
                chainId = id,
                ownerId = ownerId,
                name = request.name,
                description = request.description
            )
        )

        return updatedChain.toDto()
    }

    @Transactional
    fun deleteChain(id: UUID, ownerId: UUID) {
        val chain = chainRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Chain not found with id: $id") }

        if (chain.ownerId != ownerId) {
            throw IllegalArgumentException("You don't have permission to delete this chain")
        }

        chainRepository.delete(chain)
        
        eventPublisherService.publishEvent(
            ChainDeletedEvent(
                chainId = id,
                ownerId = ownerId,
                restaurantIds = chain.restaurants.map { it.id!! }.toSet()
            )
        )
    }

    @Transactional(readOnly = true)
    fun getChainsByOwner(ownerId: UUID): List<ChainDto> {
        return chainRepository.findByOwnerId(ownerId)
            .map { it.toDto() }
    }

    private fun Chain.toDto(): ChainDto {
        return ChainDto(
            id = id!!,
            name = name,
            description = description,
            ownerId = ownerId,
            restaurantIds = restaurants.map { it.id!! }.toSet(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 