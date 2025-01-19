package com.quisin.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotNull(message = "Reservation time is required")
    @Future(message = "Reservation time must be in the future")
    private LocalDateTime reservationTime;

    @NotNull(message = "Party size is required")
    @Min(value = 1, message = "Party size must be at least 1")
    private Integer partySize;

    private String specialRequests;

    @NotBlank(message = "Table ID is required")
    private String tableId;
} 