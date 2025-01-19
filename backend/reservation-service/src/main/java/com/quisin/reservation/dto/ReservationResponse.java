package com.quisin.reservation.dto;

import com.quisin.reservation.model.ReservationStatus;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResponse {
    private Long id;
    private String restaurantId;
    private String userId;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private String specialRequests;
    private ReservationStatus status;
    private String tableId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 