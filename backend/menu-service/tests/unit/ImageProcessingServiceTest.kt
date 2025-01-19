package com.quisin.menu.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class ImageProcessingServiceTest {

    private lateinit var imageProcessingService: ImageProcessingService
    private lateinit var tempDir: String

    @BeforeEach
    fun setup() {
        tempDir = createTempDirectory()
        imageProcessingService = ImageProcessingService(
            storagePath = tempDir,
            maxWidth = 800,
            maxHeight = 600,
            quality = 0.85f
        )
    }

    @AfterEach
    fun cleanup() {
        File(tempDir).deleteRecursively()
    }

    @Test
    fun `should process and store valid image successfully`() {
        // Given
        val image = createTestImage(1200, 900)
        val imageFile = createMultipartFile(image, "test.jpg")
        val menuItemId = "test-menu-item"

        // When
        val result = imageProcessingService.processAndStoreImage(imageFile, menuItemId).get()

        // Then
        assertTrue(File(result).exists())
        val processedImage = ImageIO.read(File(result))
        assertTrue(processedImage.width <= 800)
        assertTrue(processedImage.height <= 600)
    }

    @Test
    fun `should reject oversized image`() {
        // Given
        val image = createTestImage(5000, 5000)
        val imageFile = createMultipartFile(image, "large.jpg", size = 11 * 1024 * 1024)
        val menuItemId = "test-menu-item"

        // When/Then
        assertThrows<IllegalArgumentException> {
            imageProcessingService.processAndStoreImage(imageFile, menuItemId).get()
        }
    }

    @Test
    fun `should reject invalid file type`() {
        // Given
        val imageFile = MockMultipartFile(
            "file",
            "test.gif",
            "image/gif",
            "test data".toByteArray()
        )
        val menuItemId = "test-menu-item"

        // When/Then
        assertThrows<IllegalArgumentException> {
            imageProcessingService.processAndStoreImage(imageFile, menuItemId).get()
        }
    }

    @Test
    fun `should delete existing image successfully`() {
        // Given
        val image = createTestImage(800, 600)
        val imageFile = createMultipartFile(image, "test.jpg")
        val menuItemId = "test-menu-item"
        val imagePath = imageProcessingService.processAndStoreImage(imageFile, menuItemId).get()

        // When
        val result = imageProcessingService.deleteImage(imagePath).get()

        // Then
        assertTrue(result)
        assertFalse(File(imagePath).exists())
    }

    @Test
    fun `should handle non-existent image deletion gracefully`() {
        // Given
        val nonExistentPath = Paths.get(tempDir, "non-existent.jpg").toString()

        // When
        val result = imageProcessingService.deleteImage(nonExistentPath).get()

        // Then
        assertFalse(result)
    }

    @Test
    fun `should maintain aspect ratio when resizing`() {
        // Given
        val originalWidth = 1600
        val originalHeight = 900
        val image = createTestImage(originalWidth, originalHeight)
        val imageFile = createMultipartFile(image, "test.jpg")
        val menuItemId = "test-menu-item"

        // When
        val result = imageProcessingService.processAndStoreImage(imageFile, menuItemId).get()

        // Then
        val processedImage = ImageIO.read(File(result))
        val aspectRatioOriginal = originalWidth.toFloat() / originalHeight
        val aspectRatioProcessed = processedImage.width.toFloat() / processedImage.height
        assertEquals(aspectRatioOriginal, aspectRatioProcessed, 0.01f)
    }

    private fun createTempDirectory(): String {
        return Files.createTempDirectory("image-test").toString()
    }

    private fun createTestImage(width: Int, height: Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics
        graphics.fillRect(0, 0, width, height)
        graphics.dispose()
        return image
    }

    private fun createMultipartFile(
        image: BufferedImage,
        filename: String,
        size: Int? = null
    ): MockMultipartFile {
        val output = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", output)
        var bytes = output.toByteArray()
        
        // If size is specified, pad the byte array
        if (size != null && size > bytes.size) {
            bytes = bytes.copyOf(size)
        }
        
        return MockMultipartFile(
            "file",
            filename,
            "image/jpeg",
            bytes
        )
    }
} 