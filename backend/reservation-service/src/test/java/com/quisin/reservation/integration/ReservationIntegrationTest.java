package com.quisin.reservation.integration;

import com.quisin.reservation.config.TestConfig;
import com.quisin.reservation.dto.ReservationRequest;
import com.quisin.reservation.dto.ReservationResponse;
import com.quisin.reservation.mock.RestaurantServiceMock;
import com.quisin.reservation.model.ReservationStatus;
import com.quisin.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestConfig.class, RestaurantServiceMock.class})
public class ReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    private static final String TEST_USER_ID = "test-user";

    @Test
    void createReservation_ValidRequest_Success() {
        // Given
        ReservationRequest request = createValidReservationRequest();

        // When
        ReservationResponse response = reservationService.createReservation(request, TEST_USER_ID);

        // Then
        assertNotNull(response);
        assertEquals(request.getRestaurantId(), response.getRestaurantId());
        assertEquals(request.getTableId(), response.getTableId());
        assertEquals(TEST_USER_ID, response.getUserId());
        assertEquals(ReservationStatus.PENDING, response.getStatus());
    }

    @Test
    void createReservation_InvalidTable_ThrowsException() {
        // Given
        ReservationRequest request = createReservationRequest(
            RestaurantServiceMock.getTestRestaurantId(),
            RestaurantServiceMock.getInvalidTableId(),
            4
        );

        // When/Then
        assertThrows(IllegalStateException.class, () ->
            reservationService.createReservation(request, TEST_USER_ID)
        );
    }

    @Test
    void createReservation_InsufficientCapacity_ThrowsException() {
        // Given
        ReservationRequest request = createReservationRequest(
            RestaurantServiceMock.getTestRestaurantId(),
            RestaurantServiceMock.getValidTableId(),
            10 // Table capacity is 4
        );

        // When/Then
        assertThrows(IllegalStateException.class, () ->
            reservationService.createReservation(request, TEST_USER_ID)
        );
    }

    @Test
    void updateReservationStatus_ValidStatus_Success() {
        // Given
        ReservationRequest request = createValidReservationRequest();
        ReservationResponse reservation = reservationService.createReservation(request, TEST_USER_ID);

        // When
        ReservationResponse updatedReservation = reservationService.updateReservationStatus(
            reservation.getId(),
            ReservationStatus.CONFIRMED
        );

        // Then
        assertNotNull(updatedReservation);
        assertEquals(ReservationStatus.CONFIRMED, updatedReservation.getStatus());
    }

    @Test
    void cancelReservation_ExistingReservation_Success() {
        // Given
        ReservationRequest request = createValidReservationRequest();
        ReservationResponse reservation = reservationService.createReservation(request, TEST_USER_ID);

        // When
        reservationService.cancelReservation(reservation.getId());

        // Then
        ReservationResponse cancelledReservation = reservationService.getReservation(reservation.getId());
        assertEquals(ReservationStatus.CANCELLED, cancelledReservation.getStatus());
    }

    private ReservationRequest createValidReservationRequest() {
        return createReservationRequest(
            RestaurantServiceMock.getTestRestaurantId(),
            RestaurantServiceMock.getValidTableId(),
            4
        );
    }

    private ReservationRequest createReservationRequest(String restaurantId, String tableId, int partySize) {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(restaurantId);
        request.setTableId(tableId);
        request.setPartySize(partySize);
        request.setReservationTime(LocalDateTime.now().plusHours(2));
        request.setSpecialRequests("No special requests");
        return request;
    }
} 