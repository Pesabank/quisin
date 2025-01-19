package com.quisin.document.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.quisin.document.client.QRCodeServiceClient;
import com.quisin.document.dto.DocumentRequest;
import com.quisin.document.dto.DocumentResponse;
import com.quisin.document.model.DocumentType;
import com.quisin.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final QRCodeServiceClient qrCodeServiceClient;

    @Value("${quisin.document.brand-color}")
    private String brandColor;

    @Value("${quisin.document.brand-name}")
    private String brandName;

    @Override
    public DocumentResponse generateDocument(DocumentRequest request) {
        try {
            byte[] content;
            String fileName;
            String contentType;

            switch (request.getDocumentType()) {
                case QR_CODE:
                    if ("PDF".equalsIgnoreCase(request.getFormat())) {
                        content = generateQRCodePDF(request.getRestaurantId(), request.getTableNumber());
                        fileName = String.format("qrcode_%s_%s.pdf", request.getRestaurantId(), request.getTableNumber());
                        contentType = "application/pdf";
                    } else {
                        content = generateQRCodePNG(request.getRestaurantId(), request.getTableNumber());
                        fileName = String.format("qrcode_%s_%s.png", request.getRestaurantId(), request.getTableNumber());
                        contentType = "image/png";
                    }
                    break;
                case SALES_REPORT:
                case INVENTORY_REPORT:
                case CUSTOMER_REPORT:
                case RESERVATION_REPORT:
                case ORDER_REPORT:
                case STAFF_REPORT:
                case FINANCIAL_REPORT:
                    if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                        content = generateExcelReport(request.getRestaurantId(), request.getDocumentType(), 
                                request.getStartDate(), request.getEndDate());
                        fileName = String.format("%s_report_%s.xlsx", 
                                request.getDocumentType().toString().toLowerCase(),
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    } else {
                        content = generatePDFReport(request.getRestaurantId(), request.getDocumentType(),
                                request.getStartDate(), request.getEndDate());
                        fileName = String.format("%s_report_%s.pdf",
                                request.getDocumentType().toString().toLowerCase(),
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        contentType = "application/pdf";
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported document type: " + request.getDocumentType());
            }

            return DocumentResponse.builder()
                    .fileName(fileName)
                    .contentType(contentType)
                    .documentType(request.getDocumentType())
                    .content(content)
                    .restaurantId(request.getRestaurantId())
                    .generatedAt(LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error generating document", e);
            throw new RuntimeException("Error generating document", e);
        }
    }

    @Override
    public byte[] generateQRCodePDF(String restaurantId, String tableNumber) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);

            // Get QR code from QR code service
            ResponseEntity<byte[]> qrCodeResponse = qrCodeServiceClient.generateQRCode(restaurantId, tableNumber, "PNG");
            byte[] qrCodeImage = qrCodeResponse.getBody();

            if (qrCodeImage != null) {
                Image image = Image.getInstance(qrCodeImage);
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();
                
                // Scale and center the image
                float imageWidth = pageWidth * 0.7f; // 70% of page width
                image.scaleToFit(imageWidth, imageWidth);
                
                // Center the image
                float x = (pageWidth - image.getScaledWidth()) / 2;
                float y = (pageHeight - image.getScaledHeight()) / 2;
                image.setAbsolutePosition(x, y);
                
                document.add(image);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating QR code PDF", e);
            throw new RuntimeException("Error generating QR code PDF", e);
        }
    }

    @Override
    public byte[] generateQRCodePNG(String restaurantId, String tableNumber) {
        ResponseEntity<byte[]> response = qrCodeServiceClient.generateQRCode(restaurantId, tableNumber, "PNG");
        return response.getBody();
    }

    @Override
    public byte[] generateExcelReport(String restaurantId, DocumentType type, String startDate, String endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(type.toString() + " Report");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Add headers based on report type
            Row headerRow = sheet.createRow(0);
            createHeaders(headerRow, headerStyle, type);

            // TODO: Add data based on report type

            // Auto-size columns
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating Excel report", e);
            throw new RuntimeException("Error generating Excel report", e);
        }
    }

    @Override
    public byte[] generatePDFReport(String restaurantId, DocumentType type, String startDate, String endDate) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addReportTitle(document, type);
            addDateRange(document, startDate, endDate);
            addReportContent(document, type);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    @Override
    public byte[] generateCSVReport(String restaurantId, DocumentType type, String startDate, String endDate) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));

            // Write headers and data based on document type
            switch (type) {
                case SALES_REPORT:
                    writeSalesReportCSV(writer, restaurantId, startDate, endDate);
                    break;
                case INVENTORY_REPORT:
                    writeInventoryReportCSV(writer, restaurantId);
                    break;
                case CUSTOMER_REPORT:
                    writeCustomerReportCSV(writer, restaurantId, startDate, endDate);
                    break;
                // Add more cases for other report types
                default:
                    throw new IllegalArgumentException("Unsupported document type for CSV: " + type);
            }

            writer.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating CSV report", e);
            throw new RuntimeException("Error generating CSV report", e);
        }
    }

    @Override
    public byte[] generateRestaurantCredentials(String restaurantId) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addRestaurantCredentialsContent(document, restaurantId);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating restaurant credentials", e);
            throw new RuntimeException("Error generating restaurant credentials", e);
        }
    }

    @Override
    public byte[] generateAuditReport(String restaurantId, String startDate, String endDate) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addAuditReportContent(document, restaurantId, startDate, endDate);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating audit report", e);
            throw new RuntimeException("Error generating audit report", e);
        }
    }

    @Override
    public byte[] generateAnalyticsReport(String restaurantId, String startDate, String endDate, String format) {
        try {
            if ("PDF".equalsIgnoreCase(format)) {
                return generatePDFReport(restaurantId, DocumentType.ANALYTICS_REPORT, startDate, endDate);
            } else if ("EXCEL".equalsIgnoreCase(format)) {
                return generateExcelReport(restaurantId, DocumentType.ANALYTICS_REPORT, startDate, endDate);
            } else if ("CSV".equalsIgnoreCase(format)) {
                return generateCSVReport(restaurantId, DocumentType.ANALYTICS_REPORT, startDate, endDate);
            } else {
                throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (Exception e) {
            log.error("Error generating analytics report", e);
            throw new RuntimeException("Error generating analytics report", e);
        }
    }

    @Override
    public byte[] generatePerformanceReport(String restaurantId, String period) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addPerformanceReportContent(document, restaurantId, period);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating performance report", e);
            throw new RuntimeException("Error generating performance report", e);
        }
    }

    @Override
    public byte[] generateInventoryReport(String restaurantId) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addInventoryReportContent(document, restaurantId);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating inventory report", e);
            throw new RuntimeException("Error generating inventory report", e);
        }
    }

    @Override
    public byte[] generateTableReport(String restaurantId) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addTableReportContent(document, restaurantId);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating table report", e);
            throw new RuntimeException("Error generating table report", e);
        }
    }

    @Override
    public byte[] generateMenuReport(String restaurantId) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);
            addMenuReportContent(document, restaurantId);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating menu report", e);
            throw new RuntimeException("Error generating menu report", e);
        }
    }

    @Override
    public byte[] generateStaffCredentials(String restaurantId, String staffId, String username, String password) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addBrandingHeader(document);

            // Add Staff Credentials Content
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font credentialsFont = FontFactory.getFont(FontFactory.COURIER, 14, Font.BOLD);

            // Title
            Paragraph title = new Paragraph("Staff Login Credentials", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Credentials Info
            Paragraph info = new Paragraph();
            info.add(new Chunk("Please keep this information secure.\n\n", contentFont));
            info.add(new Chunk("Your login credentials for the Quisin Restaurant Management System are:\n\n", contentFont));
            info.setSpacingAfter(20);
            document.add(info);

            // Username and Password
            PdfPTable credentialsTable = new PdfPTable(2);
            credentialsTable.setWidthPercentage(80);
            credentialsTable.setSpacingBefore(10);
            credentialsTable.setSpacingAfter(10);

            // Add Username Row
            PdfPCell labelCell = new PdfPCell(new Phrase("Username:", contentFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            credentialsTable.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(username, credentialsFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            credentialsTable.addCell(valueCell);

            // Add Password Row
            labelCell = new PdfPCell(new Phrase("Password:", contentFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            credentialsTable.addCell(labelCell);

            valueCell = new PdfPCell(new Phrase(password, credentialsFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            credentialsTable.addCell(valueCell);

            document.add(credentialsTable);

            // Add Security Notice
            Paragraph notice = new Paragraph();
            notice.setSpacingBefore(20);
            notice.add(new Chunk("Important Security Notice:\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            notice.add(new Chunk("1. Please change your password upon first login\n", contentFont));
            notice.add(new Chunk("2. Keep these credentials confidential\n", contentFont));
            notice.add(new Chunk("3. Never share your login information\n", contentFont));
            notice.add(new Chunk("4. Contact your administrator if you suspect unauthorized access\n", contentFont));
            document.add(notice);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating staff credentials", e);
            throw new RuntimeException("Error generating staff credentials", e);
        }
    }

    private void addBrandingHeader(Document document) throws DocumentException {
        Paragraph header = new Paragraph();
        header.setAlignment(Element.ALIGN_CENTER);
        
        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.decode(brandColor));
        header.add(new Chunk(brandName, brandFont));
        header.add(Chunk.NEWLINE);
        header.add(Chunk.NEWLINE);
        
        document.add(header);
    }

    private void addReportTitle(Document document, DocumentType type) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph(formatReportTitle(type), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addDateRange(Document document, String startDate, String endDate) throws DocumentException {
        if (startDate != null && endDate != null) {
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph dateRange = new Paragraph(String.format("Period: %s to %s", startDate, endDate), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            dateRange.setSpacingAfter(20);
            document.add(dateRange);
        }
    }

    private void addReportContent(Document document, DocumentType type) throws DocumentException {
        // Create table with appropriate columns based on report type
        PdfPTable table = createTableForReportType(type);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        addTableHeaders(table, type);

        // TODO: Add actual data rows based on report type

        document.add(table);
    }

    private PdfPTable createTableForReportType(DocumentType type) {
        switch (type) {
            case SALES_REPORT:
                return new PdfPTable(new float[]{2, 1, 1, 1}); // Date, Items, Amount, Status
            case INVENTORY_REPORT:
                return new PdfPTable(new float[]{2, 1, 1, 1}); // Item, Quantity, Price, Value
            case CUSTOMER_REPORT:
                return new PdfPTable(new float[]{2, 1, 1, 1}); // Customer, Orders, Total Spent, Last Visit
            default:
                return new PdfPTable(4);
        }
    }

    private void addTableHeaders(PdfPTable table, DocumentType type) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        BaseColor headerBackground = BaseColor.decode(brandColor);

        String[] headers = getHeadersForReportType(type);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerBackground);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private String[] getHeadersForReportType(DocumentType type) {
        switch (type) {
            case SALES_REPORT:
                return new String[]{"Date", "Items", "Amount", "Status"};
            case INVENTORY_REPORT:
                return new String[]{"Item", "Quantity", "Price", "Value"};
            case CUSTOMER_REPORT:
                return new String[]{"Customer", "Orders", "Total Spent", "Last Visit"};
            case RESERVATION_REPORT:
                return new String[]{"Date", "Customer", "Table", "Status"};
            case ORDER_REPORT:
                return new String[]{"Order ID", "Items", "Total", "Status"};
            case STAFF_REPORT:
                return new String[]{"Name", "Role", "Orders", "Performance"};
            case FINANCIAL_REPORT:
                return new String[]{"Category", "Revenue", "Expenses", "Profit"};
            default:
                return new String[]{"Column 1", "Column 2", "Column 3", "Column 4"};
        }
    }

    private void createHeaders(Row headerRow, CellStyle style, DocumentType type) {
        String[] headers = getHeadersForReportType(type);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private String formatReportTitle(DocumentType type) {
        return type.toString()
                .replace("_", " ")
                .toLowerCase()
                .substring(0, 1).toUpperCase() +
                type.toString()
                        .replace("_", " ")
                        .toLowerCase()
                        .substring(1);
    }

    private void writeSalesReportCSV(CSVWriter writer, String restaurantId, String startDate, String endDate) {
        // Write headers
        writer.writeNext(new String[]{"Date", "Order ID", "Total", "Items", "Status"});
        
        // TODO: Fetch and write sales data
    }

    private void writeInventoryReportCSV(CSVWriter writer, String restaurantId) {
        // Write headers
        writer.writeNext(new String[]{"Item", "Quantity", "Unit", "Min Threshold", "Cost"});
        
        // TODO: Fetch and write inventory data
    }

    private void writeCustomerReportCSV(CSVWriter writer, String restaurantId, String startDate, String endDate) {
        // Write headers
        writer.writeNext(new String[]{"Customer", "Orders", "Total Spent", "Last Visit"});
        
        // TODO: Fetch and write customer data
    }

    private void addRestaurantCredentialsContent(Document document, String restaurantId) throws DocumentException {
        // TODO: Add restaurant credentials content
    }

    private void addAuditReportContent(Document document, String restaurantId, String startDate, String endDate) throws DocumentException {
        // TODO: Add audit report content
    }

    private void addPerformanceReportContent(Document document, String restaurantId, String period) throws DocumentException {
        // TODO: Add performance report content
    }

    private void addInventoryReportContent(Document document, String restaurantId) throws DocumentException {
        // TODO: Add inventory report content
    }

    private void addTableReportContent(Document document, String restaurantId) throws DocumentException {
        // TODO: Add table report content
    }

    private void addMenuReportContent(Document document, String restaurantId) throws DocumentException {
        // TODO: Add menu report content
    }
} 