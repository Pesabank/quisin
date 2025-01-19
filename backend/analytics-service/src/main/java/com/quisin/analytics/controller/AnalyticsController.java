package com.quisin.analytics.controller;

import com.quisin.analytics.dto.AnalyticsRequest;
import com.quisin.analytics.dto.AnalyticsResponse;
import com.quisin.analytics.model.AnalyticsPeriod;
import com.quisin.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics API", description = "Endpoints for managing restaurant analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @PostMapping
    @Operation(summary = "Get analytics data based on specified criteria")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@RequestBody AnalyticsRequest request) {
        return ResponseEntity.ok(analyticsService.getAnalytics(request));
    }

    @GetMapping("/{restaurantId}/history")
    @Operation(summary = "Get historical analytics data for a restaurant")
    public ResponseEntity<List<AnalyticsResponse>> getAnalyticsHistory(
            @PathVariable String restaurantId,
            @RequestParam AnalyticsPeriod period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(analyticsService.getAnalyticsHistory(restaurantId, period, startDate, endDate));
    }

    @PostMapping("/{restaurantId}/generate")
    @Operation(summary = "Manually trigger analytics generation for a restaurant")
    public ResponseEntity<Void> generateAnalytics(@PathVariable String restaurantId) {
        analyticsService.generateAnalytics(restaurantId);
        return ResponseEntity.ok().build();
    }
} 