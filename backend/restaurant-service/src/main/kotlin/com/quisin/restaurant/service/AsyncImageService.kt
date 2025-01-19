package com.quisin.restaurant.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.math.min

@Service
class AsyncImageService(
    private val imageService: ImageService,
    private val eventPublisher: EventPublisherService
) {
    companion object {
        private const val THUMBNAIL_SIZE: Int = 200
        private const val PREVIEW_SIZE: Int = 800
    }

    @Async
    fun processAndSaveImage(file: MultipartFile, restaurantId: UUID): String {
        // Save original image first
        val originalFileName: String = imageService.saveImage(file, restaurantId)

        try {
            // Process thumbnail
            processImageVariant(file, restaurantId, THUMBNAIL_SIZE, "thumbnail")
            
            // Process preview
            processImageVariant(file, restaurantId, PREVIEW_SIZE, "preview")
        } catch (e: Exception) {
            // Log error but don't fail the whole process
            println("Failed to process image variants: ${e.message}")
        }

        return originalFileName
    }

    private fun processImageVariant(file: MultipartFile, restaurantId: UUID, size: Int, prefix: String) {
        val originalImage: BufferedImage = ImageIO.read(file.inputStream)
        val resizedImage: BufferedImage = resizeImage(originalImage, size)
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        
        val extension: String = file.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
        ImageIO.write(resizedImage, extension, outputStream)
        
        val variantFile: File = File.createTempFile(prefix, ".$extension")
        variantFile.writeBytes(outputStream.toByteArray())
        
        imageService.saveImage(createMultipartFile(variantFile, file, prefix), restaurantId)
        
        variantFile.delete()
    }

    private fun createMultipartFile(variantFile: File, originalFile: MultipartFile, prefix: String): MultipartFile {
        return object : MultipartFile {
            override fun getInputStream(): InputStream = variantFile.inputStream()
            override fun getName(): String = "${prefix}_${originalFile.originalFilename}"
            override fun getOriginalFilename(): String = "${prefix}_${originalFile.originalFilename}"
            override fun getContentType(): String = originalFile.contentType ?: "image/jpeg"
            override fun isEmpty(): Boolean = variantFile.length() == 0L
            override fun getSize(): Long = variantFile.length()
            override fun getBytes(): ByteArray = variantFile.readBytes()
            override fun transferTo(dest: File) {
                variantFile.copyTo(dest, true)
            }
        }
    }

    private fun resizeImage(originalImage: BufferedImage, targetSize: Int): BufferedImage {
        val originalWidth: Int = originalImage.width
        val originalHeight: Int = originalImage.height
        
        val widthRatio: Double = targetSize.toDouble() / originalWidth.toDouble()
        val heightRatio: Double = targetSize.toDouble() / originalHeight.toDouble()
        val ratio: Double = minOf(widthRatio, heightRatio)
        
        val targetWidth: Int = (originalWidth * ratio).toInt()
        val targetHeight: Int = (originalHeight * ratio).toInt()

        val resizedImage: BufferedImage = BufferedImage(targetWidth, targetHeight, originalImage.type)
        val graphics: java.awt.Graphics2D = resizedImage.createGraphics()
        
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics.dispose()

        return resizedImage
    }
} 