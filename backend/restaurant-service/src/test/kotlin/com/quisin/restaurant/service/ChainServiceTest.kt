package com.quisin.restaurant.service

import com.quisin.restaurant.domain.Chain
import com.quisin.restaurant.dto.ChainDto
import com.quisin.restaurant.dto.CreateChainRequest
import com.quisin.restaurant.dto.UpdateChainRequest
import com.quisin.restaurant.event.ChainCreatedEvent
import com.quisin.restaurant.event.ChainDeletedEvent
import com.quisin.restaurant.event.ChainUpdatedEvent
import com.quisin.restaurant.repository.ChainRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.UUID

class ChainServiceTest {
    private lateinit var chainRepository: ChainRepository
    private lateinit var eventPublisherService: EventPublisherService
    private lateinit var chainService: ChainService

    @BeforeEach
    fun setup() {
        chainRepository = mockk()
        eventPublisherService = mockk()
        chainService = ChainService(chainRepository, eventPublisherService)
    }

    @Test
    fun `test create chain`() {
        val ownerId = UUID.randomUUID()
        val request = CreateChainRequest(
            name = "Test Chain",
            description = "Test Description"
        )

        val chain = Chain(
            id = UUID.randomUUID(),
            name = request.name,
            description = request.description,
            ownerId = ownerId,
            restaurants = mutableSetOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { chainRepository.existsByNameAndOwnerId(any(), any()) } returns false
        every { chainRepository.save(any()) } returns chain
        every { eventPublisherService.publishEvent(any<ChainCreatedEvent>()) } just Runs

        val result = chainService.createChain(request, ownerId)

        assertNotNull(result.id)
        assertEquals(request.name, result.name)
        assertEquals(request.description, result.description)
        assertEquals(ownerId, result.ownerId)
        assertTrue(result.restaurantIds.isEmpty())

        verify(exactly = 1) { chainRepository.save(any()) }
        verify(exactly = 1) { eventPublisherService.publishEvent(any<ChainCreatedEvent>()) }
    }

    @Test
    fun `test update chain`() {
        val chainId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val request = UpdateChainRequest(
            name = "Updated Chain",
            description = "Updated Description"
        )

        val existingChain = Chain(
            id = chainId,
            name = "Old Name",
            description = "Old Description",
            ownerId = ownerId,
            restaurants = mutableSetOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { chainRepository.findById(chainId) } returns java.util.Optional.of(existingChain)
        every { chainRepository.save(any()) } answers { firstArg() }
        every { eventPublisherService.publishEvent(any<ChainUpdatedEvent>()) } just Runs

        val result = chainService.updateChain(chainId, request, ownerId)

        assertEquals(chainId, result.id)
        assertEquals(request.name, result.name)
        assertEquals(request.description, result.description)
        assertEquals(ownerId, result.ownerId)

        verify(exactly = 1) { chainRepository.save(any()) }
        verify(exactly = 1) { eventPublisherService.publishEvent(any<ChainUpdatedEvent>()) }
    }

    @Test
    fun `test delete chain`() {
        val chainId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val chain = Chain(
            id = chainId,
            name = "Test Chain",
            description = "Test Description",
            ownerId = ownerId,
            restaurants = mutableSetOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { chainRepository.findById(chainId) } returns java.util.Optional.of(chain)
        every { chainRepository.delete(chain) } just Runs
        every { eventPublisherService.publishEvent(any<ChainDeletedEvent>()) } just Runs

        chainService.deleteChain(chainId, ownerId)

        verify(exactly = 1) { chainRepository.delete(chain) }
        verify(exactly = 1) { eventPublisherService.publishEvent(any<ChainDeletedEvent>()) }
    }

    @Test
    fun `test get chain`() {
        val chainId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val chain = Chain(
            id = chainId,
            name = "Test Chain",
            description = "Test Description",
            ownerId = ownerId,
            restaurants = mutableSetOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { chainRepository.findById(chainId) } returns java.util.Optional.of(chain)

        val result = chainService.getChain(chainId)

        assertEquals(chainId, result.id)
        assertEquals(chain.name, result.name)
        assertEquals(chain.description, result.description)
        assertEquals(ownerId, result.ownerId)
        assertTrue(result.restaurantIds.isEmpty())

        verify(exactly = 1) { chainRepository.findById(chainId) }
    }

    @Test
    fun `test get chains by owner`() {
        val ownerId = UUID.randomUUID()
        val chain1 = Chain(
            id = UUID.randomUUID(),
            name = "Chain 1",
            description = "Description 1",
            ownerId = ownerId,
            restaurants = mutableSetOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val chain2 = Chain(
            id = UUID.randomUUID(),
            name = "Chain 2",
            description = "Description 2",
            ownerId = ownerId,
            restaurants = mutableSetOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { chainRepository.findByOwnerId(ownerId) } returns listOf(chain1, chain2)

        val results = chainService.getChainsByOwner(ownerId)

        assertEquals(2, results.size)
        assertEquals(chain1.name, results[0].name)
        assertEquals(chain2.name, results[1].name)

        verify(exactly = 1) { chainRepository.findByOwnerId(ownerId) }
    }
} 