package com.quisin.qrcode.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

@Service
class QRCodeGenerationService {

    data class QRCodeGenerationOptions(
        val tableNumber: String,
        val width: Int = 300,
        val height: Int = 300,
        val format: OutputFormat = OutputFormat.PNG
    )

    enum class OutputFormat {
        PNG, PDF
    }

    // Add Quisin Branding Color
    private val QUISIN_ORANGE = Color(255, 107, 0) // #FF6B00

    fun generateTableQRCode(options: QRCodeGenerationOptions): ByteArray {
        // Generate QR Code content
        val qrContent = "QUISIN_TABLE:${options.tableNumber}"
        
        // Create QR Code with custom color
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            qrContent, 
            BarcodeFormat.QR_CODE, 
            options.width, 
            options.height
        )

        // Create custom color matrix
        val quicinOrange = 0xFFFF6B00.toInt() // Quisin Orange color
        val customColorMatrix = BufferedImage(options.width, options.height, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until options.width) {
            for (y in 0 until options.height) {
                customColorMatrix.setRGB(x, y, if (bitMatrix.get(x, y)) quicinOrange else 0x00000000)
            }
        }

        // Add table number text
        val annotatedImage = addTableNumberToImage(customColorMatrix, options.tableNumber)

        // Convert to byte array based on format
        return when (options.format) {
            OutputFormat.PNG -> convertImageToPngBytes(annotatedImage)
            OutputFormat.PDF -> convertImageToPdf(annotatedImage)
        }
    }

    private fun addTableNumberToImage(
        originalImage: BufferedImage, 
        tableNumber: String
    ): BufferedImage {
        val graphics = originalImage.createGraphics()
        
        // Set font color to black for readability
        graphics.color = Color.BLACK
        graphics.font = Font("Arial", Font.BOLD, 20)
        
        // Calculate text position
        val fontMetrics = graphics.fontMetrics
        val textWidth = fontMetrics.stringWidth(tableNumber)
        val x = (originalImage.width - textWidth) / 2
        val y = originalImage.height - 20

        // Draw text
        graphics.drawString(tableNumber, x, y)
        graphics.dispose()

        return originalImage
    }

    private fun convertImageToPngBytes(image: BufferedImage): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        return baos.toByteArray()
    }

    private fun convertImageToPdf(image: BufferedImage): ByteArray {
        val document = Document()
        val baos = ByteArrayOutputStream()
        
        PdfWriter.getInstance(document, baos)
        document.open()

        // Convert BufferedImage to iText Image
        val iTextImage = Image.getInstance(image, null)
        
        // Center the image in the PDF
        iTextImage.alignment = Image.ALIGN_CENTER
        document.add(iTextImage)
        
        document.close()
        
        return baos.toByteArray()
    }

    fun generateUniqueTableNumber(): String {
        // Generate a unique table number 
        // Format could be Q-001, Q-002, etc.
        val uuid = UUID.randomUUID().toString().take(4).uppercase()
        return "Q-$uuid"
    }
}
