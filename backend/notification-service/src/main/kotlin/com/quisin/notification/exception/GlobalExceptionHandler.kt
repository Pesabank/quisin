package com.quisin.notification.exception

import com.quisin.notification.service.NotificationPreferenceException
import com.quisin.notification.service.NotificationSendingException
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

    @ExceptionHandler(NotificationPreferenceException::class)
    fun handleNotificationPreferenceException(
        ex: NotificationPreferenceException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Notification Preference Error",
            details = ex.message ?: "No additional details"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotificationSendingException::class)
    fun handleNotificationSendingException(
        ex: NotificationSendingException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "Notification Sending Error",
            details = ex.message ?: "Failed to send notification"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "An unexpected error occurred",
            details = ex.message ?: "No details available"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
