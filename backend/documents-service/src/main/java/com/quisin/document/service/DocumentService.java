package com.quisin.document.service;

import com.quisin.document.dto.DocumentRequest;
import com.quisin.document.dto.DocumentResponse;
import com.quisin.document.model.DocumentType;

public interface DocumentService {
    DocumentResponse generateDocument(DocumentRequest request);
    byte[] generateQRCodePDF(String restaurantId, String tableNumber);
    byte[] generateQRCodePNG(String restaurantId, String tableNumber);
    byte[] generateExcelReport(String restaurantId, DocumentType type, String startDate, String endDate);
    byte[] generatePDFReport(String restaurantId, DocumentType type, String startDate, String endDate);
    byte[] generateCSVReport(String restaurantId, DocumentType type, String startDate, String endDate);
    byte[] generateRestaurantCredentials(String restaurantId);
    byte[] generateAuditReport(String restaurantId, String startDate, String endDate);
    byte[] generateAnalyticsReport(String restaurantId, String startDate, String endDate, String format);
    byte[] generatePerformanceReport(String restaurantId, String period);
    byte[] generateInventoryReport(String restaurantId);
    byte[] generateTableReport(String restaurantId);
    byte[] generateMenuReport(String restaurantId);
    byte[] generateStaffCredentials(String restaurantId, String staffId, String username, String password);
} 