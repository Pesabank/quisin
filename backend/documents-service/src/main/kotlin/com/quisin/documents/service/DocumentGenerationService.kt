package com.quisin.documents.service

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.opencsv.CSVWriter
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class DocumentGenerationService {

    // Quisin Branding Colors
    private val primaryColor = BaseColor(255, 107, 0) // Correct Quisin Orange #FF6B00
    private val secondaryColor = BaseColor(255, 107, 0) // Using same color for consistency

    data class InvoiceData(
        val invoiceNumber: String,
        val customerName: String,
        val orderDate: LocalDateTime,
        val items: List<InvoiceItem>,
        val totalAmount: BigDecimal
    )

    data class InvoiceItem(
        val name: String,
        val quantity: Int,
        val unitPrice: BigDecimal
    )

    fun generateInvoicePDF(invoiceData: InvoiceData): ByteArray {
        val document = Document()
        val baos = ByteArrayOutputStream()

        PdfWriter.getInstance(document, baos)
        document.open()

        // Add Quisin Branding
        addQuisinHeader(document)

        // Invoice Details
        addInvoiceDetails(document, invoiceData)

        // Invoice Items Table
        addInvoiceItemsTable(document, invoiceData)

        // Total and Footer
        addInvoiceFooter(document, invoiceData)

        document.close()
        return baos.toByteArray()
    }

    private fun addQuisinHeader(document: Document) {
        val titleFont = Font(Font.FontFamily.HELVETICA, 24f, Font.BOLD, primaryColor)
        val title = Paragraph("Quisin", titleFont)
        title.alignment = Element.ALIGN_CENTER
        document.add(title)

        val subtitleFont = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL, BaseColor.DARK_GRAY)
        val subtitle = Paragraph("Restaurant Invoice", subtitleFont)
        subtitle.alignment = Element.ALIGN_CENTER
        document.add(subtitle)

        document.add(Paragraph("\n"))
    }

    private fun addInvoiceDetails(document: Document, invoiceData: InvoiceData) {
        val detailsFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)
        
        val detailsParagraph = Paragraph().apply {
            add(Chunk("Invoice Number: ${invoiceData.invoiceNumber}\n", detailsFont))
            add(Chunk("Customer: ${invoiceData.customerName}\n", detailsFont))
            add(Chunk("Date: ${invoiceData.orderDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}\n", detailsFont))
        }
        
        document.add(detailsParagraph)
        document.add(Paragraph("\n"))
    }

    private fun addInvoiceItemsTable(document: Document, invoiceData: InvoiceData) {
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 1f, 1f))

        // Table Header
        val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.WHITE)
        val headerCell = PdfPCell(Paragraph("Item", headerFont)).apply {
            backgroundColor = primaryColor
            horizontalAlignment = Element.ALIGN_CENTER
        }
        table.addCell(headerCell)

        headerCell.phrase = Paragraph("Quantity", headerFont)
        table.addCell(headerCell)

        headerCell.phrase = Paragraph("Price", headerFont)
        table.addCell(headerCell)

        // Table Rows
        invoiceData.items.forEach { item ->
            table.addCell(item.name)
            table.addCell(item.quantity.toString())
            table.addCell("$${item.unitPrice}")
        }

        document.add(table)
        document.add(Paragraph("\n"))
    }

    private fun addInvoiceFooter(document: Document, invoiceData: InvoiceData) {
        val totalFont = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, secondaryColor)
        val totalParagraph = Paragraph("Total: $${invoiceData.totalAmount}", totalFont)
        totalParagraph.alignment = Element.ALIGN_RIGHT
        document.add(totalParagraph)
    }

    fun generateExcelReport(data: List<Map<String, Any>>): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Quisin Report")

        // Create header row
        val headerRow = sheet.createRow(0)
        data.firstOrNull()?.keys?.forEachIndexed { index, key ->
            headerRow.createCell(index).setCellValue(key)
        }

        // Populate data rows
        data.forEachIndexed { rowIndex, rowData ->
            val row = sheet.createRow(rowIndex + 1)
            rowData.values.forEachIndexed { colIndex, value ->
                row.createCell(colIndex).setCellValue(value.toString())
            }
        }

        val baos = ByteArrayOutputStream()
        workbook.write(baos)
        workbook.close()
        return baos.toByteArray()
    }

    fun generateCSVReport(data: List<Map<String, Any>>): ByteArray {
        val baos = ByteArrayOutputStream()
        val csvWriter = CSVWriter(baos.writer())

        // Write headers
        val headers = data.firstOrNull()?.keys?.toTypedArray() ?: arrayOf()
        csvWriter.writeNext(headers)

        // Write data rows
        data.forEach { rowData ->
            val row = headers.map { rowData[it]?.toString() ?: "" }.toTypedArray()
            csvWriter.writeNext(row)
        }

        csvWriter.close()
        return baos.toByteArray()
    }
}
