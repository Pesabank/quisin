package com.quisin.qrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResponse {
    private String qrCodeUrl;
    private byte[] qrCodeImage;
    private String tableNumber;
    private String restaurantName;
    private String restaurantLocation;
} 