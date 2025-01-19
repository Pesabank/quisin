package com.quisin.reservation.controller;

import com.quisin.reservation.dto.ReservationRequest;
import com.quisin.reservation.dto.ReservationResponse;
import com.quisin.reservation.model.ReservationStatus;
import com.quisin.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return reservationService.createReservation(request, jwt.getSubject());
    }

    @PutMapping("/{id}")
    public ReservationResponse updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request
    ) {
        return reservationService.updateReservation(id, request);
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservation(@PathVariable Long id) {
        return reservationService.getReservation(id);
    }

    @GetMapping("/user")
    public List<ReservationResponse> getUserReservations(@AuthenticationPrincipal Jwt jwt) {
        return reservationService.getUserReservations(jwt.getSubject());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<ReservationResponse> getRestaurantReservations(@PathVariable String restaurantId) {
        return reservationService.getRestaurantReservations(restaurantId);
    }

    @GetMapping("/restaurant/{restaurantId}/status/{status}")
    public List<ReservationResponse> getRestaurantReservationsByStatus(
            @PathVariable String restaurantId,
            @PathVariable ReservationStatus status
    ) {
        return reservationService.getRestaurantReservationsByStatus(restaurantId, status);
    }

    @GetMapping("/restaurant/{restaurantId}/time-range")
    public List<ReservationResponse> getRestaurantReservationsByTimeRange(
            @PathVariable String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        return reservationService.getRestaurantReservationsByTimeRange(restaurantId, startTime, endTime);
    }

    @PatchMapping("/{id}/status")
    public ReservationResponse updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status
    ) {
        return reservationService.updateReservationStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }

    @GetMapping("/restaurant/{restaurantId}/table/{tableId}/availability")
    public ResponseEntity<Boolean> checkTableAvailability(
            @PathVariable String restaurantId,
            @PathVariable String tableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time
    ) {
        boolean isAvailable = reservationService.isTableAvailable(restaurantId, tableId, time);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/restaurant/{restaurantId}/available-tables")
    public ResponseEntity<List<String>> getAvailableTables(
            @PathVariable String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time
    ) {
        List<String> availableTables = reservationService.getAvailableTables(restaurantId, time);
        return ResponseEntity.ok(availableTables);
    }

    @GetMapping("/restaurant/{restaurantId}/active-count")
    public ResponseEntity<Integer> getActiveReservationsCount(
            @PathVariable String restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time
    ) {
        int count = reservationService.getActiveReservationsCount(restaurantId, time);
        return ResponseEntity.ok(count);
    }
} 