package com.quisin.reservation.service.impl;

import com.quisin.reservation.client.TableDto;
import com.quisin.reservation.dto.ReservationRequest;
import com.quisin.reservation.dto.ReservationResponse;
import com.quisin.reservation.event.ReservationEvent;
import com.quisin.reservation.event.ReservationEventPublisher;
import com.quisin.reservation.model.Reservation;
import com.quisin.reservation.model.ReservationStatus;
import com.quisin.reservation.repository.ReservationRepository;
import com.quisin.reservation.service.ReservationService;
import com.quisin.reservation.service.RestaurantIntegrationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationEventPublisher eventPublisher;
    private final RestaurantIntegrationService restaurantService;

    @Value("${reservation.min-notice-minutes}")
    private int minNoticeMinutes;

    @Value("${reservation.max-per-slot}")
    private int maxPerSlot;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String userId) {
        validateReservationTime(request.getReservationTime());
        validateRestaurantOperating(request.getRestaurantId(), request.getReservationTime());
        validateTableAvailability(request.getRestaurantId(), request.getTableId(), request.getReservationTime());
        validateTableCapacity(request.getRestaurantId(), request.getTableId(), request.getPartySize());
        validateSlotCapacity(request.getRestaurantId(), request.getReservationTime());

        Reservation reservation = Reservation.builder()
            .restaurantId(request.getRestaurantId())
            .userId(userId)
            .reservationTime(request.getReservationTime())
            .partySize(request.getPartySize())
            .specialRequests(request.getSpecialRequests())
            .tableId(request.getTableId())
            .status(ReservationStatus.PENDING)
            .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        eventPublisher.publishReservationCreated(createEventFromReservation(savedReservation));

        return mapToResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        Reservation reservation = getReservationEntity(id);
        validateReservationTime(request.getReservationTime());
        validateTableAvailability(request.getRestaurantId(), request.getTableId(), request.getReservationTime());

        reservation.setReservationTime(request.getReservationTime());
        reservation.setPartySize(request.getPartySize());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setTableId(request.getTableId());

        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Publish event
        eventPublisher.publishReservationUpdated(createEventFromReservation(updatedReservation));

        return mapToResponse(updatedReservation);
    }

    @Override
    public ReservationResponse getReservation(Long id) {
        return mapToResponse(getReservationEntity(id));
    }

    @Override
    public List<ReservationResponse> getUserReservations(String userId) {
        return reservationRepository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getRestaurantReservations(String restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getRestaurantReservationsByStatus(String restaurantId, ReservationStatus status) {
        return reservationRepository.findByRestaurantIdAndStatus(restaurantId, status).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getRestaurantReservationsByTimeRange(
        String restaurantId,
        LocalDateTime startTime,
        LocalDateTime endTime
    ) {
        return reservationRepository.findByRestaurantIdAndTimeRange(restaurantId, startTime, endTime).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = getReservationEntity(id);
        reservation.setStatus(status);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Publish event
        eventPublisher.publishReservationStatusChanged(createEventFromReservation(updatedReservation));

        return mapToResponse(updatedReservation);
    }

    @Override
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = getReservationEntity(id);
        reservation.setStatus(ReservationStatus.CANCELLED);
        
        Reservation cancelledReservation = reservationRepository.save(reservation);
        
        // Publish event
        eventPublisher.publishReservationCancelled(createEventFromReservation(cancelledReservation));
    }

    @Override
    public boolean isTableAvailable(String restaurantId, String tableId, LocalDateTime time) {
        return !reservationRepository.existsByRestaurantIdAndTableIdAndReservationTimeAndStatusNot(
            restaurantId, tableId, time, ReservationStatus.CANCELLED
        );
    }

    @Override
    public List<String> getAvailableTables(String restaurantId, LocalDateTime time) {
        List<String> occupiedTableIds = reservationRepository.findOccupiedTableIds(restaurantId, time);
        List<TableDto> allTables = restaurantService.getRestaurantTables(restaurantId);
        
        return allTables.stream()
            .filter(table -> !occupiedTableIds.contains(table.getId()))
            .filter(table -> table.getStatus() == TableDto.TableStatus.AVAILABLE)
            .map(TableDto::getId)
            .collect(Collectors.toList());
    }

    @Override
    public int getActiveReservationsCount(String restaurantId, LocalDateTime time) {
        return reservationRepository.countActiveReservationsForTimeSlot(restaurantId, time);
    }

    @Override
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void handleExpiredReservations() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30); // Consider reservations expired after 30 minutes
        List<Reservation> expiredReservations = reservationRepository.findExpiredPendingReservations(cutoffTime);
        
        expiredReservations.forEach(reservation -> {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        });
    }

    private Reservation getReservationEntity(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));
    }

    private void validateReservationTime(LocalDateTime reservationTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutesUntilReservation = ChronoUnit.MINUTES.between(now, reservationTime);
        
        if (minutesUntilReservation < minNoticeMinutes) {
            throw new IllegalArgumentException(
                "Reservation must be made at least " + minNoticeMinutes + " minutes in advance"
            );
        }
    }

    private void validateTableAvailability(String restaurantId, String tableId, LocalDateTime time) {
        if (!isTableAvailable(restaurantId, tableId, time)) {
            throw new IllegalStateException("Table is not available at the requested time");
        }
    }

    private void validateSlotCapacity(String restaurantId, LocalDateTime time) {
        int currentReservations = getActiveReservationsCount(restaurantId, time);
        if (currentReservations >= maxPerSlot) {
            throw new IllegalStateException("Maximum reservations reached for this time slot");
        }
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
            .id(reservation.getId())
            .restaurantId(reservation.getRestaurantId())
            .userId(reservation.getUserId())
            .reservationTime(reservation.getReservationTime())
            .partySize(reservation.getPartySize())
            .specialRequests(reservation.getSpecialRequests())
            .status(reservation.getStatus())
            .tableId(reservation.getTableId())
            .createdAt(reservation.getCreatedAt())
            .updatedAt(reservation.getUpdatedAt())
            .build();
    }

    private ReservationEvent createEventFromReservation(Reservation reservation) {
        return ReservationEvent.builder()
            .reservationId(reservation.getId())
            .restaurantId(reservation.getRestaurantId())
            .userId(reservation.getUserId())
            .reservationTime(reservation.getReservationTime())
            .partySize(reservation.getPartySize())
            .tableId(reservation.getTableId())
            .status(reservation.getStatus())
            .build();
    }

    private void validateRestaurantOperating(String restaurantId, LocalDateTime reservationTime) {
        if (!restaurantService.isRestaurantOperating(restaurantId, reservationTime)) {
            throw new IllegalStateException("Restaurant is not operating at the requested time");
        }
    }

    private void validateTableCapacity(String restaurantId, String tableId, int partySize) {
        TableDto table = restaurantService.getTable(restaurantId, tableId);
        if (table == null) {
            throw new IllegalArgumentException("Table not found");
        }
        if (table.getCapacity() < partySize) {
            throw new IllegalStateException("Table capacity is insufficient for the party size");
        }
        if (table.getStatus() == TableDto.TableStatus.OUT_OF_SERVICE) {
            throw new IllegalStateException("Selected table is out of service");
        }
    }
} 