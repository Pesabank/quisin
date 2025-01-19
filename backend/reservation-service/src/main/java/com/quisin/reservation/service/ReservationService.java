package com.quisin.reservation.service;

import com.quisin.reservation.dto.ReservationRequest;
import com.quisin.reservation.dto.ReservationResponse;
import com.quisin.reservation.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request, String userId);
    
    ReservationResponse updateReservation(Long id, ReservationRequest request);
    
    ReservationResponse getReservation(Long id);
    
    List<ReservationResponse> getUserReservations(String userId);
    
    List<ReservationResponse> getRestaurantReservations(String restaurantId);
    
    List<ReservationResponse> getRestaurantReservationsByStatus(String restaurantId, ReservationStatus status);
    
    List<ReservationResponse> getRestaurantReservationsByTimeRange(
        String restaurantId,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
    
    ReservationResponse updateReservationStatus(Long id, ReservationStatus status);
    
    void cancelReservation(Long id);
    
    boolean isTableAvailable(String restaurantId, String tableId, LocalDateTime time);
    
    List<String> getAvailableTables(String restaurantId, LocalDateTime time);
    
    int getActiveReservationsCount(String restaurantId, LocalDateTime time);
    
    void handleExpiredReservations();
} 