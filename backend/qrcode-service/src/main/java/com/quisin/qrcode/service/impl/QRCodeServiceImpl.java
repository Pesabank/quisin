package com.quisin.qrcode.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.quisin.qrcode.dto.QRCodeResponse;
import com.quisin.qrcode.dto.TableRequest;
import com.quisin.qrcode.dto.TableResponse;
import com.quisin.qrcode.exception.TableAlreadyExistsException;
import com.quisin.qrcode.exception.TableNotFoundException;
import com.quisin.qrcode.model.RestaurantTable;
import com.quisin.qrcode.repository.TableRepository;
import com.quisin.qrcode.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeServiceImpl implements QRCodeService {

    private final TableRepository tableRepository;
    private static final int QR_CODE_SIZE = 300;
    private static final int PADDING = 40;
    private static final int HEADER_HEIGHT = 60;
    private static final int FOOTER_HEIGHT = 80;
    private static final int TOTAL_HEIGHT = QR_CODE_SIZE + HEADER_HEIGHT + FOOTER_HEIGHT + (PADDING * 2);
    private static final int TOTAL_WIDTH = QR_CODE_SIZE + (PADDING * 2);
    private static final Color BRAND_COLOR = Color.decode("#FF6B00");
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // Dark gray
    private static final int CORNER_RADIUS = 20;

    @Value("${quisin.qrcode.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public TableResponse createTable(TableRequest request) {
        if (tableRepository.existsByTableNumberAndRestaurantId(request.getTableNumber(), request.getRestaurantId())) {
            throw new TableAlreadyExistsException("Table " + request.getTableNumber() + " already exists");
        }

        RestaurantTable table = RestaurantTable.builder()
                .restaurantId(request.getRestaurantId())
                .tableNumber(request.getTableNumber())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .qrCodeUrl(generateQRCodeUrl(request.getRestaurantId(), request.getTableNumber()))
                .isActive(true)
                .build();

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    public List<TableResponse> getTablesByRestaurantId(String restaurantId) {
        return tableRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TableResponse getTableByNumber(String restaurantId, String tableNumber) {
        return tableRepository.findByTableNumberAndRestaurantId(tableNumber, restaurantId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new TableNotFoundException("Table " + tableNumber + " not found"));
    }

    @Override
    public QRCodeResponse generateQRCode(String restaurantId, String tableNumber, String format) {
        RestaurantTable table = tableRepository.findByTableNumberAndRestaurantId(tableNumber, restaurantId)
                .orElseThrow(() -> new TableNotFoundException("Table " + tableNumber + " not found"));

        try {
            // Generate QR code
            String qrCodeUrl = table.getQrCodeUrl();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Create the complete image with padding
            BufferedImage completeImage = new BufferedImage(TOTAL_WIDTH, TOTAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = completeImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Set background
            graphics.setColor(BACKGROUND_COLOR);
            graphics.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

            // Add rounded rectangle border
            graphics.setColor(BRAND_COLOR);
            graphics.setStroke(new BasicStroke(2f));
            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    PADDING/2, PADDING/2,
                    TOTAL_WIDTH - PADDING,
                    TOTAL_HEIGHT - PADDING,
                    CORNER_RADIUS, CORNER_RADIUS
            );
            graphics.draw(roundedRectangle);

            // Add header with table number
            graphics.setColor(BRAND_COLOR);
            graphics.setFont(new Font("Arial", Font.BOLD, 32));
            String headerText = "#" + table.getTableNumber();
            FontMetrics metrics = graphics.getFontMetrics();
            int headerX = (TOTAL_WIDTH - metrics.stringWidth(headerText)) / 2;
            graphics.drawString(headerText, headerX, HEADER_HEIGHT);

            // Draw QR code with padding
            graphics.drawImage(qrCodeImage, PADDING, HEADER_HEIGHT, null);

            // Add divider line
            graphics.setColor(new Color(230, 230, 230)); // Light gray
            graphics.setStroke(new BasicStroke(1f));
            graphics.drawLine(
                    PADDING,
                    TOTAL_HEIGHT - FOOTER_HEIGHT,
                    TOTAL_WIDTH - PADDING,
                    TOTAL_HEIGHT - FOOTER_HEIGHT
            );

            // Add footer text
            graphics.setColor(TEXT_COLOR);
            graphics.setFont(new Font("Arial", Font.BOLD, 14));
            String footerText = "Scan this QR code to:";
            metrics = graphics.getFontMetrics();
            int footerX = (TOTAL_WIDTH - metrics.stringWidth(footerText)) / 2;
            graphics.drawString(footerText, footerX, TOTAL_HEIGHT - FOOTER_HEIGHT + 30);

            // Add menu text with bullet point
            graphics.setFont(new Font("Arial", Font.PLAIN, 14));
            String menuText = "• View our digital menu";
            metrics = graphics.getFontMetrics();
            int menuX = (TOTAL_WIDTH - metrics.stringWidth(menuText)) / 2;
            graphics.drawString(menuText, menuX, TOTAL_HEIGHT - FOOTER_HEIGHT + 55);

            graphics.dispose();

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(completeImage, format.toUpperCase(), outputStream);

            return QRCodeResponse.builder()
                    .qrCodeUrl(qrCodeUrl)
                    .qrCodeImage(outputStream.toByteArray())
                    .tableNumber(table.getTableNumber())
                    .build();
        } catch (Exception e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    @Override
    public String suggestNextTableNumber(String restaurantId, String pattern) {
        List<String> existingNumbers = tableRepository.findLastTableNumberByRestaurantId(restaurantId);
        if (existingNumbers.isEmpty()) {
            return pattern;
        }

        String lastNumber = existingNumbers.get(0);
        return generateNextTableNumber(lastNumber);
    }

    @Override
    @Transactional
    public void deleteTable(String restaurantId, String tableNumber) {
        RestaurantTable table = tableRepository.findByTableNumberAndRestaurantId(tableNumber, restaurantId)
                .orElseThrow(() -> new TableNotFoundException("Table " + tableNumber + " not found"));
        tableRepository.delete(table);
    }

    @Override
    @Transactional
    public TableResponse updateTable(String restaurantId, String tableNumber, TableRequest request) {
        RestaurantTable table = tableRepository.findByTableNumberAndRestaurantId(tableNumber, restaurantId)
                .orElseThrow(() -> new TableNotFoundException("Table " + tableNumber + " not found"));

        table.setCapacity(request.getCapacity());
        table.setDescription(request.getDescription());

        if (!tableNumber.equals(request.getTableNumber())) {
            if (tableRepository.existsByTableNumberAndRestaurantId(request.getTableNumber(), restaurantId)) {
                throw new TableAlreadyExistsException("Table " + request.getTableNumber() + " already exists");
            }
            table.setTableNumber(request.getTableNumber());
            table.setQrCodeUrl(generateQRCodeUrl(restaurantId, request.getTableNumber()));
        }

        return mapToResponse(tableRepository.save(table));
    }

    private String generateQRCodeUrl(String restaurantId, String tableNumber) {
        return baseUrl + "/restaurant/" + restaurantId + "/table/" + tableNumber;
    }

    private String generateNextTableNumber(String currentNumber) {
        // Extract numeric and non-numeric parts
        Pattern pattern = Pattern.compile("([^0-9]*)([0-9]+)");
        Matcher matcher = pattern.matcher(currentNumber);

        if (matcher.find()) {
            String prefix = matcher.group(1);
            String numberPart = matcher.group(2);
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return prefix + String.format("%0" + numberPart.length() + "d", nextNumber);
        }

        // If no number found, append "1"
        return currentNumber + "1";
    }

    private TableResponse mapToResponse(RestaurantTable table) {
        return TableResponse.builder()
                .id(table.getId())
                .restaurantId(table.getRestaurantId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .description(table.getDescription())
                .qrCodeUrl(table.getQrCodeUrl())
                .isActive(table.getIsActive())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();
    }
} 