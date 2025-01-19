package com.quisin.reservation.event;

import com.quisin.reservation.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEvent {
    private String eventId;
    private String eventType;
    private Long reservationId;
    private String restaurantId;
    private String userId;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private String tableId;
    private ReservationStatus status;
    private LocalDateTime timestamp;
} 