package com.quisin.menu.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageService(
    @Value("\${quisin.images.upload-dir:uploads/menu-images}")
    private val uploadDir: String,
    
    @Value("\${quisin.images.max-size:5242880}")
    private val maxFileSize: Long = 5 * 1024 * 1024 // 5MB
) {
    private val imageExtensions = setOf("jpg", "jpeg", "png", "gif")
    private val thumbnailSize = 200
    private val previewSize = 800

    init {
        val directory = Paths.get(uploadDir)
        if (!Files.exists(directory)) {
            Files.createDirectories(directory)
        }
    }

    fun saveImage(file: MultipartFile, menuItemId: String): String {
        validateImage(file)

        val extension = getFileExtension(file.originalFilename!!)
        val fileName = "${UUID.randomUUID()}_${menuItemId}.$extension"
        val targetPath = Paths.get(uploadDir, fileName)

        // Save original image
        Files.copy(file.inputStream, targetPath)

        // Generate and save thumbnail
        val thumbnailFileName = "thumb_$fileName"
        val thumbnailPath = Paths.get(uploadDir, thumbnailFileName)
        generateThumbnail(file, thumbnailPath, thumbnailSize)

        // Generate and save preview
        val previewFileName = "preview_$fileName"
        val previewPath = Paths.get(uploadDir, previewFileName)
        generateThumbnail(file, previewPath, previewSize)

        return fileName
    }

    fun deleteImage(fileName: String) {
        val filePath = Paths.get(uploadDir, fileName)
        val thumbnailPath = Paths.get(uploadDir, "thumb_$fileName")
        val previewPath = Paths.get(uploadDir, "preview_$fileName")

        Files.deleteIfExists(filePath)
        Files.deleteIfExists(thumbnailPath)
        Files.deleteIfExists(previewPath)
    }

    fun getImagePath(fileName: String): Path {
        return Paths.get(uploadDir, fileName)
    }

    private fun validateImage(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        if (file.size > maxFileSize) {
            throw IllegalArgumentException("File size exceeds maximum limit of 5MB")
        }

        val extension = getFileExtension(file.originalFilename!!)
        if (!imageExtensions.contains(extension.lowercase())) {
            throw IllegalArgumentException("Invalid file type. Allowed types: ${imageExtensions.joinToString(", ")}")
        }
    }

    private fun generateThumbnail(file: MultipartFile, targetPath: Path, size: Int) {
        val originalImage = ImageIO.read(file.inputStream)
        val thumbnail = createThumbnail(originalImage, size)
        
        val extension = getFileExtension(file.originalFilename!!)
        ImageIO.write(thumbnail, extension, targetPath.toFile())
    }

    private fun createThumbnail(img: BufferedImage, size: Int): BufferedImage {
        val originalWidth = img.width
        val originalHeight = img.height
        
        val ratio = minOf(size.toFloat() / originalWidth, size.toFloat() / originalHeight)
        val targetWidth = (originalWidth * ratio).toInt()
        val targetHeight = (originalHeight * ratio).toInt()

        val resized = BufferedImage(targetWidth, targetHeight, img.type)
        val g = resized.createGraphics()
        g.drawImage(img, 0, 0, targetWidth, targetHeight, null)
        g.dispose()

        return resized
    }

    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }
} 