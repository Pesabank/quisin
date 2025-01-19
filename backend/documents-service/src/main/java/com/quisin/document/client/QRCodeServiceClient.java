package com.quisin.document.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "qrcode-service", path = "/api/v1/qrcode/tables")
public interface QRCodeServiceClient {

    @GetMapping("/restaurant/{restaurantId}/table/{tableNumber}/qrcode")
    ResponseEntity<byte[]> generateQRCode(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("tableNumber") String tableNumber,
            @RequestParam(defaultValue = "PNG") String format);
} 