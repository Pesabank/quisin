package com.quisin.analytics.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: HttpStatus,
    val message: String,
    val details: String
)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "An unexpected error occurred",
            details = ex.message ?: "No details available"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AnalyticsNotFoundException::class)
    fun handleAnalyticsNotFound(ex: AnalyticsNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND,
            message = "Analytics not found",
            details = ex.message ?: "No details available"
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }
}

class AnalyticsNotFoundException(message: String) : RuntimeException(message)
