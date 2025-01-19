package com.quisin.order.service.service;

import com.quisin.order.service.client.MenuServiceClient;
import com.quisin.order.service.client.MenuItemResponse;
import com.quisin.order.service.dto.OrderItemRequest;
import com.quisin.order.service.exception.MenuItemNotAvailableException;
import com.quisin.order.service.exception.RestaurantMenuItemMismatchException;
import com.quisin.order.service.service.impl.MenuValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuValidationServiceTest {

    @Mock
    private MenuServiceClient menuServiceClient;

    @InjectMocks
    private MenuValidationServiceImpl menuValidationService;

    private static final Long RESTAURANT_ID = 1L;
    private static final Long MENU_ITEM_ID = 1L;
    private static final Long MENU_ITEM_ID_2 = 2L;

    @Test
    void validateMenuItems_Success() {
        // Arrange
        OrderItemRequest request = OrderItemRequest.builder()
                .menuItemId(MENU_ITEM_ID)
                .quantity(2)
                .build();

        MenuItemResponse menuItem = MenuItemResponse.builder()
                .id(MENU_ITEM_ID)
                .name("Test Item")
                .price(BigDecimal.valueOf(10.0))
                .available(true)
                .restaurantId(RESTAURANT_ID)
                .build();

        when(menuServiceClient.getMenuItem(RESTAURANT_ID, MENU_ITEM_ID)).thenReturn(menuItem);

        // Act
        menuValidationService.validateMenuItems(RESTAURANT_ID, List.of(request));

        // Assert
        verify(menuServiceClient, times(1)).getMenuItem(RESTAURANT_ID, MENU_ITEM_ID);
    }

    @Test
    void validateMenuItems_ItemNotAvailable() {
        // Arrange
        OrderItemRequest request = OrderItemRequest.builder()
                .menuItemId(MENU_ITEM_ID)
                .quantity(2)
                .build();

        MenuItemResponse menuItem = MenuItemResponse.builder()
                .id(MENU_ITEM_ID)
                .name("Test Item")
                .price(BigDecimal.valueOf(10.0))
                .available(false)
                .restaurantId(RESTAURANT_ID)
                .build();

        when(menuServiceClient.getMenuItem(RESTAURANT_ID, MENU_ITEM_ID)).thenReturn(menuItem);

        // Act & Assert
        assertThatThrownBy(() -> menuValidationService.validateMenuItems(RESTAURANT_ID, List.of(request)))
                .isInstanceOf(MenuItemNotAvailableException.class)
                .hasMessageContaining("Menu item is not available");
        verify(menuServiceClient, times(1)).getMenuItem(RESTAURANT_ID, MENU_ITEM_ID);
    }

    @Test
    void validateMenuItems_WrongRestaurant() {
        // Arrange
        OrderItemRequest request = OrderItemRequest.builder()
                .menuItemId(MENU_ITEM_ID)
                .quantity(2)
                .build();

        MenuItemResponse menuItem = MenuItemResponse.builder()
                .id(MENU_ITEM_ID)
                .name("Test Item")
                .price(BigDecimal.valueOf(10.0))
                .available(true)
                .restaurantId(999L)
                .build();

        when(menuServiceClient.getMenuItem(RESTAURANT_ID, MENU_ITEM_ID)).thenReturn(menuItem);

        // Act & Assert
        assertThatThrownBy(() -> menuValidationService.validateMenuItems(RESTAURANT_ID, List.of(request)))
                .isInstanceOf(RestaurantMenuItemMismatchException.class)
                .hasMessageContaining("Menu item does not belong to restaurant");
        verify(menuServiceClient, times(1)).getMenuItem(RESTAURANT_ID, MENU_ITEM_ID);
    }
} 