package com.quisin.document.controller;

import com.quisin.document.dto.DocumentRequest;
import com.quisin.document.dto.DocumentResponse;
import com.quisin.document.model.DocumentType;
import com.quisin.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateDocument(@Valid @RequestBody DocumentRequest request) {
        DocumentResponse response = documentService.generateDocument(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .body(response.getContent());
    }

    @GetMapping("/qrcode/{restaurantId}/{tableNumber}")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable String restaurantId,
            @PathVariable String tableNumber,
            @RequestParam(defaultValue = "PNG") String format) {

        DocumentRequest request = DocumentRequest.builder()
                .restaurantId(restaurantId)
                .tableNumber(tableNumber)
                .documentType(DocumentType.QR_CODE)
                .format(format)
                .build();

        DocumentResponse response = documentService.generateDocument(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .body(response.getContent());
    }

    @GetMapping("/report/{restaurantId}/{type}")
    public ResponseEntity<byte[]> generateReport(
            @PathVariable String restaurantId,
            @PathVariable DocumentType type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "PDF") String format) {

        DocumentRequest request = DocumentRequest.builder()
                .restaurantId(restaurantId)
                .documentType(type)
                .startDate(startDate)
                .endDate(endDate)
                .format(format)
                .build();

        DocumentResponse response = documentService.generateDocument(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .body(response.getContent());
    }

    @GetMapping("/credentials/{restaurantId}")
    public ResponseEntity<byte[]> generateRestaurantCredentials(@PathVariable String restaurantId) {
        byte[] content = documentService.generateRestaurantCredentials(restaurantId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"restaurant_credentials_" + restaurantId + ".pdf\"")
                .body(content);
    }

    @GetMapping("/audit/{restaurantId}")
    public ResponseEntity<byte[]> generateAuditReport(
            @PathVariable String restaurantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        byte[] content = documentService.generateAuditReport(restaurantId, startDate, endDate);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"audit_report_" + restaurantId + ".pdf\"")
                .body(content);
    }

    @GetMapping("/analytics/{restaurantId}")
    public ResponseEntity<byte[]> generateAnalyticsReport(
            @PathVariable String restaurantId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "PDF") String format) {
        
        byte[] content = documentService.generateAnalyticsReport(restaurantId, startDate, endDate, format);
        String extension = format.toLowerCase();
        String contentType = switch (format.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF_VALUE;
            case "EXCEL" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "CSV" -> "text/csv";
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"analytics_report_" + restaurantId + "." + extension + "\"")
                .body(content);
    }

    @GetMapping("/performance/{restaurantId}")
    public ResponseEntity<byte[]> generatePerformanceReport(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "DAILY") String period) {
        
        byte[] content = documentService.generatePerformanceReport(restaurantId, period);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"performance_report_" + restaurantId + ".pdf\"")
                .body(content);
    }

    @GetMapping("/inventory/{restaurantId}")
    public ResponseEntity<byte[]> generateInventoryReport(@PathVariable String restaurantId) {
        byte[] content = documentService.generateInventoryReport(restaurantId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"inventory_report_" + restaurantId + ".pdf\"")
                .body(content);
    }

    @GetMapping("/tables/{restaurantId}")
    public ResponseEntity<byte[]> generateTableReport(@PathVariable String restaurantId) {
        byte[] content = documentService.generateTableReport(restaurantId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"table_report_" + restaurantId + ".pdf\"")
                .body(content);
    }

    @GetMapping("/menu/{restaurantId}")
    public ResponseEntity<byte[]> generateMenuReport(@PathVariable String restaurantId) {
        byte[] content = documentService.generateMenuReport(restaurantId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"menu_report_" + restaurantId + ".pdf\"")
                .body(content);
    }

    @GetMapping("/staff/credentials/{restaurantId}/{staffId}")
    public ResponseEntity<byte[]> generateStaffCredentials(
            @PathVariable String restaurantId,
            @PathVariable String staffId,
            @RequestParam String username,
            @RequestParam String password) {
        
        byte[] content = documentService.generateStaffCredentials(restaurantId, staffId, username, password);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"staff_credentials_" + staffId + ".pdf\"")
                .body(content);
    }
} 