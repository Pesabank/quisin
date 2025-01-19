package com.quisin.qrcode.controller;

import com.quisin.qrcode.dto.QRCodeResponse;
import com.quisin.qrcode.dto.TableRequest;
import com.quisin.qrcode.dto.TableResponse;
import com.quisin.qrcode.service.QRCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tables")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @PostMapping
    public ResponseEntity<TableResponse> createTable(@Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(qrCodeService.createTable(request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<TableResponse>> getTablesByRestaurantId(@PathVariable String restaurantId) {
        return ResponseEntity.ok(qrCodeService.getTablesByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/table/{tableNumber}")
    public ResponseEntity<TableResponse> getTableByNumber(
            @PathVariable String restaurantId,
            @PathVariable String tableNumber) {
        return ResponseEntity.ok(qrCodeService.getTableByNumber(restaurantId, tableNumber));
    }

    @GetMapping("/restaurant/{restaurantId}/table/{tableNumber}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable String restaurantId,
            @PathVariable String tableNumber,
            @RequestParam(defaultValue = "PNG") String format) {
        
        QRCodeResponse qrCode = qrCodeService.generateQRCode(restaurantId, tableNumber, format);
        
        String contentType = format.equalsIgnoreCase("PDF") ? 
                MediaType.APPLICATION_PDF_VALUE : 
                MediaType.IMAGE_PNG_VALUE;
        
        String filename = String.format("qrcode_%s_%s.%s", 
                restaurantId, 
                tableNumber, 
                format.toLowerCase());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(qrCode.getQrCodeImage());
    }

    @GetMapping("/restaurant/{restaurantId}/suggest-number")
    public ResponseEntity<String> suggestNextTableNumber(
            @PathVariable String restaurantId,
            @RequestParam String pattern) {
        return ResponseEntity.ok(qrCodeService.suggestNextTableNumber(restaurantId, pattern));
    }

    @DeleteMapping("/restaurant/{restaurantId}/table/{tableNumber}")
    public ResponseEntity<Void> deleteTable(
            @PathVariable String restaurantId,
            @PathVariable String tableNumber) {
        qrCodeService.deleteTable(restaurantId, tableNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/restaurant/{restaurantId}/table/{tableNumber}")
    public ResponseEntity<TableResponse> updateTable(
            @PathVariable String restaurantId,
            @PathVariable String tableNumber,
            @Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(qrCodeService.updateTable(restaurantId, tableNumber, request));
    }
} 