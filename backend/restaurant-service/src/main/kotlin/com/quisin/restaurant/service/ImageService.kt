package com.quisin.restaurant.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class ImageService(
    @Value("\${quisin.images.upload-dir:uploads/images}")
    private val uploadDir: String
) {
    private val imageExtensions = setOf("jpg", "jpeg", "png", "gif")
    private val maxFileSize = 5 * 1024 * 1024 // 5MB

    init {
        val directory = Paths.get(uploadDir)
        if (!Files.exists(directory)) {
            Files.createDirectories(directory)
        }
    }

    fun saveImage(file: MultipartFile, restaurantId: UUID): String {
        validateImage(file)

        val extension = getFileExtension(file.originalFilename!!)
        val fileName = "${UUID.randomUUID()}_${restaurantId}.$extension"
        val filePath = Paths.get(uploadDir, fileName)
        
        Files.copy(file.inputStream, filePath)
        return fileName
    }

    fun deleteImage(fileName: String) {
        val filePath = Paths.get(uploadDir, fileName)
        Files.deleteIfExists(filePath)
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
            throw IllegalArgumentException("Invalid file type. Only ${imageExtensions.joinToString(", ")} are allowed")
        }
    }

    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "jpg")
    }
} 