package com.quisin.menu.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt
import org.springframework.beans.factory.annotation.Value
import java.awt.RenderingHints
import java.awt.Image
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture

@Service
class ImageProcessingService(
    @Value("\${quisin.images.storage-path}")
    private val storagePath: String,
    @Value("\${quisin.images.max-width}")
    private val maxWidth: Int = 1200,
    @Value("\${quisin.images.max-height}")
    private val maxHeight: Int = 1200,
    @Value("\${quisin.images.quality}")
    private val quality: Float = 0.85f
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val allowedExtensions = listOf("jpg", "jpeg", "png")
    
    init {
        Files.createDirectories(Paths.get(storagePath))
    }

    @Async("imageProcessingExecutor")
    fun processAndStoreImage(file: MultipartFile, menuItemId: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            try {
                validateImage(file)
                val originalImage = ImageIO.read(file.inputStream)
                val processedImage = resizeImage(originalImage)
                val imagePath = saveImage(processedImage, getFileExtension(file.originalFilename))
                logger.info("Successfully processed and stored image for menuItemId: $menuItemId")
                imagePath
            } catch (e: Exception) {
                logger.error("Failed to process image for menuItemId: $menuItemId", e)
                throw e
            }
        }
    }

    @Async("imageProcessingExecutor")
    fun deleteImage(imagePath: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                val file = File(imagePath)
                if (file.exists()) {
                    file.delete()
                    logger.info("Successfully deleted image: $imagePath")
                    true
                } else {
                    logger.warn("Image not found: $imagePath")
                    false
                }
            } catch (e: Exception) {
                logger.error("Failed to delete image: $imagePath", e)
                throw e
            }
        }
    }

    private fun validateImage(file: MultipartFile) {
        val extension = getFileExtension(file.originalFilename)
        require(extension in allowedExtensions) {
            "Invalid file type. Allowed types: ${allowedExtensions.joinToString(", ")}"
        }
        require(file.size <= 10 * 1024 * 1024) { "File size exceeds 10MB limit" }
    }

    private fun resizeImage(originalImage: BufferedImage): BufferedImage {
        val (width, height) = calculateDimensions(originalImage.width, originalImage.height)
        
        val resizedImage = BufferedImage(width, height, TYPE_INT_RGB)
        val graphics = resizedImage.createGraphics()
        
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        graphics.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
        graphics.dispose()
        
        return resizedImage
    }

    private fun calculateDimensions(width: Int, height: Int): Pair<Int, Int> {
        var newWidth = width
        var newHeight = height
        
        if (width > maxWidth) {
            newWidth = maxWidth
            newHeight = (height * (maxWidth.toFloat() / width)).roundToInt()
        }
        
        if (newHeight > maxHeight) {
            newHeight = maxHeight
            newWidth = (width * (maxHeight.toFloat() / height)).roundToInt()
        }
        
        return Pair(newWidth, newHeight)
    }

    private fun saveImage(image: BufferedImage, extension: String): String {
        val fileName = "${UUID.randomUUID()}.$extension"
        val filePath = Paths.get(storagePath, fileName).toString()
        
        ByteArrayOutputStream().use { output ->
            ImageIO.write(image, extension, output)
            Files.write(Paths.get(filePath), output.toByteArray())
        }
        
        return filePath
    }

    private fun getFileExtension(fileName: String?): String {
        return fileName?.substringAfterLast('.')?.lowercase() ?: "jpg"
    }
} 