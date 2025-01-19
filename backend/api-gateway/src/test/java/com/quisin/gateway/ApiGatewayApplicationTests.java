package com.quisin.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointShouldBeAccessible() {
        webTestClient
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void authEndpointShouldBeAccessible() {
        webTestClient
            .get()
            .uri("/api/auth/health")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void fallbackEndpointsShouldWork() {
        webTestClient
            .get()
            .uri("/fallback/auth")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(body -> assertThat(body).contains("Auth Service is currently unavailable"));
    }
} 