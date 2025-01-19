package com.quisin.qrcode.service;

import com.quisin.qrcode.dto.QRCodeResponse;
import com.quisin.qrcode.dto.TableRequest;
import com.quisin.qrcode.dto.TableResponse;

import java.util.List;

public interface QRCodeService {
    TableResponse createTable(TableRequest request);
    List<TableResponse> getTablesByRestaurantId(String restaurantId);
    TableResponse getTableByNumber(String restaurantId, String tableNumber);
    QRCodeResponse generateQRCode(String restaurantId, String tableNumber, String format);
    String suggestNextTableNumber(String restaurantId, String pattern);
    void deleteTable(String restaurantId, String tableNumber);
    TableResponse updateTable(String restaurantId, String tableNumber, TableRequest request);
} 