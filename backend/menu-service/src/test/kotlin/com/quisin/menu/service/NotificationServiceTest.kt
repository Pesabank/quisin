package com.quisin.menu.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.client.RestTemplate
import org.springframework.http.ResponseEntity

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @Mock
    private lateinit var notificationRestTemplate: RestTemplate

    @InjectMocks
    private lateinit var notificationService: NotificationService

    @Test
    fun `should send menu created notification successfully`() {
        // Given
        val menuId = "test-menu-id"
        val restaurantId = "test-restaurant-id"
        val expectedNotification = NotificationRequest(
            type = "MENU_CREATED",
            targetId = menuId,
            restaurantId = restaurantId,
            message = "New menu has been created",
            priority = "NORMAL"
        )
        
        `when`(notificationRestTemplate.postForEntity(
            eq("/api/v1/notifications"),
            eq(expectedNotification),
            eq(Void::class.java),
            emptyArray<Any>()
        )).thenReturn(ResponseEntity.ok().build())

        // When
        notificationService.sendMenuCreatedNotification(menuId, restaurantId)

        // Then
        verify(notificationRestTemplate).postForEntity(
            eq("/api/v1/notifications"),
            eq(expectedNotification),
            eq(Void::class.java),
            emptyArray<Any>()
        )
    }

    @Test
    fun `should handle menu created notification failure gracefully`() {
        // Given
        val menuId = "test-menu-id"
        val restaurantId = "test-restaurant-id"
        
        `when`(notificationRestTemplate.postForEntity(
            any<String>(),
            any(),
            eq(Void::class.java),
            emptyArray<Any>()
        )).thenThrow(RuntimeException("Network error"))

        // When
        notificationService.sendMenuCreatedNotification(menuId, restaurantId)

        // Then
        verify(notificationRestTemplate).postForEntity(
            any<String>(),
            any(),
            eq(Void::class.java),
            emptyArray<Any>()
        )
        // No exception should be thrown
    }

    @Test
    fun `should send menu updated notification successfully`() {
        // Given
        val menuId = "test-menu-id"
        val restaurantId = "test-restaurant-id"
        val expectedNotification = NotificationRequest(
            type = "MENU_UPDATED",
            targetId = menuId,
            restaurantId = restaurantId,
            message = "Menu has been updated",
            priority = "NORMAL"
        )
        
        `when`(notificationRestTemplate.postForEntity(
            eq("/api/v1/notifications"),
            eq(expectedNotification),
            eq(Void::class.java),
            emptyArray<Any>()
        )).thenReturn(ResponseEntity.ok().build())

        // When
        notificationService.sendMenuUpdatedNotification(menuId, restaurantId)

        // Then
        verify(notificationRestTemplate).postForEntity(
            eq("/api/v1/notifications"),
            eq(expectedNotification),
            eq(Void::class.java),
            emptyArray<Any>()
        )
    }

    @Test
    fun `should send menu deleted notification successfully`() {
        // Given
        val menuId = "test-menu-id"
        val restaurantId = "test-restaurant-id"
        val expectedNotification = NotificationRequest(
            type = "MENU_DELETED",
            targetId = menuId,
            restaurantId = restaurantId,
            message = "Menu has been deleted",
            priority = "HIGH"
        )
        
        `when`(notificationRestTemplate.postForEntity(
            eq("/api/v1/notifications"),
            eq(expectedNotification),
            eq(Void::class.java),
            emptyArray<Any>()
        )).thenReturn(ResponseEntity.ok().build())

        // When
        notificationService.sendMenuDeletedNotification(menuId, restaurantId)

        // Then
        verify(notificationRestTemplate).postForEntity(
            eq("/api/v1/notifications"),
            eq(expectedNotification),
            eq(Void::class.java),
            emptyArray<Any>()
        )
    }
} 