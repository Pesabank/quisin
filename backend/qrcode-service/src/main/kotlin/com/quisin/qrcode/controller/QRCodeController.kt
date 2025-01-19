package com.quisin.qrcode.controller

import com.quisin.qrcode.service.QRCodeGenerationService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/qr")
class QRCodeController(
    private val qrCodeGenerationService: QRCodeGenerationService
) {
    @PostMapping("/table")
    fun generateTableQRCode(
        @RequestParam(required = false) tableNumber: String? = null,
        @RequestParam(defaultValue = "300") width: Int = 300,
        @RequestParam(defaultValue = "300") height: Int = 300,
        @RequestParam(defaultValue = "PNG") format: QRCodeGenerationService.OutputFormat = QRCodeGenerationService.OutputFormat.PNG
    ): ResponseEntity<ByteArray> {
        // Use provided table number or generate a new one
        val finalTableNumber = tableNumber ?: qrCodeGenerationService.generateUniqueTableNumber()

        // Generate QR Code
        val qrCodeBytes = qrCodeGenerationService.generateTableQRCode(
            QRCodeGenerationService.QRCodeGenerationOptions(
                tableNumber = finalTableNumber,
                width = width,
                height = height,
                format = format
            )
        )

        // Prepare response headers
        val headers = HttpHeaders().apply {
            contentType = when (format) {
                QRCodeGenerationService.OutputFormat.PNG -> MediaType.IMAGE_PNG
                QRCodeGenerationService.OutputFormat.PDF -> MediaType.APPLICATION_PDF
            }
            contentDisposition = org.springframework.http.ContentDisposition.builder("attachment")
                .filename("table_${finalTableNumber}_qr.${format.name.lowercase()}")
                .build()
        }

        return ResponseEntity(qrCodeBytes, headers, HttpStatus.OK)
    }

    @GetMapping("/generate-table-number")
    fun generateTableNumber(): ResponseEntity<String> {
        return ResponseEntity.ok(qrCodeGenerationService.generateUniqueTableNumber())
    }
}
