package com.quisin.documents.controller

import com.quisin.documents.service.DocumentGenerationService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val documentGenerationService: DocumentGenerationService
) {
    @PostMapping("/invoice/pdf")
    fun generateInvoicePDF(
        @RequestBody invoiceRequest: InvoiceRequest
    ): ResponseEntity<ByteArray> {
        val invoiceData = DocumentGenerationService.InvoiceData(
            invoiceNumber = invoiceRequest.invoiceNumber,
            customerName = invoiceRequest.customerName,
            orderDate = invoiceRequest.orderDate,
            items = invoiceRequest.items.map { 
                DocumentGenerationService.InvoiceItem(
                    name = it.name,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice
                )
            },
            totalAmount = invoiceRequest.items.sumOf { it.quantity * it.unitPrice }
        )

        val pdfBytes = documentGenerationService.generateInvoicePDF(invoiceData)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_PDF
            contentDisposition = org.springframework.http.ContentDisposition.builder("attachment")
                .filename("invoice_${invoiceData.invoiceNumber}.pdf")
                .build()
        }

        return ResponseEntity(pdfBytes, headers, HttpStatus.OK)
    }

    @PostMapping("/report/excel")
    fun generateExcelReport(
        @RequestBody reportData: List<Map<String, Any>>
    ): ResponseEntity<ByteArray> {
        val excelBytes = documentGenerationService.generateExcelReport(reportData)

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            contentDisposition = org.springframework.http.ContentDisposition.builder("attachment")
                .filename("quisin_report_${System.currentTimeMillis()}.xlsx")
                .build()
        }

        return ResponseEntity(excelBytes, headers, HttpStatus.OK)
    }

    @PostMapping("/report/csv")
    fun generateCSVReport(
        @RequestBody reportData: List<Map<String, Any>>
    ): ResponseEntity<ByteArray> {
        val csvBytes = documentGenerationService.generateCSVReport(reportData)

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("text/csv")
            contentDisposition = org.springframework.http.ContentDisposition.builder("attachment")
                .filename("quisin_report_${System.currentTimeMillis()}.csv")
                .build()
        }

        return ResponseEntity(csvBytes, headers, HttpStatus.OK)
    }

    // Data Transfer Objects
    data class InvoiceRequest(
        val invoiceNumber: String,
        val customerName: String,
        val orderDate: LocalDateTime,
        val items: List<InvoiceItemRequest>
    )

    data class InvoiceItemRequest(
        val name: String,
        val quantity: Int,
        val unitPrice: BigDecimal
    )
}
