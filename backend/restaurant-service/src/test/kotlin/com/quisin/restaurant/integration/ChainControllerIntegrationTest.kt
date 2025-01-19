package com.quisin.restaurant.integration

import com.quisin.restaurant.dto.ChainDto
import com.quisin.restaurant.dto.CreateChainRequest
import com.quisin.restaurant.dto.UpdateChainRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChainControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val ownerId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        // Setup test data if needed
    }

    @Test
    fun `test create chain`() {
        val request = CreateChainRequest(
            name = "Test Chain",
            description = "Test Description"
        )

        val response = restTemplate.postForEntity(
            "/api/v1/chains",
            request,
            ChainDto::class.java
        )

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.name == request.name)
        assert(response.body?.description == request.description)
    }

    @Test
    fun `test get chain`() {
        val chainId = UUID.randomUUID()
        val response = restTemplate.getForEntity(
            "/api/v1/chains/$chainId",
            ChainDto::class.java
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.id == chainId)
    }

    @Test
    fun `test update chain`() {
        val chainId = UUID.randomUUID()
        val request = UpdateChainRequest(
            name = "Updated Chain",
            description = "Updated Description"
        )

        val response = restTemplate.exchange(
            "/api/v1/chains/$chainId",
            HttpMethod.PUT,
            HttpEntity(request),
            ChainDto::class.java
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.name == request.name)
        assert(response.body?.description == request.description)
    }

    @Test
    fun `test delete chain`() {
        val chainId = UUID.randomUUID()
        restTemplate.delete("/api/v1/chains/$chainId")
    }
} 