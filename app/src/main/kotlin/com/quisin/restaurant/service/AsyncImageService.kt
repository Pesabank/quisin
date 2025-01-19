  @Async
    override fun processAndStoreImage(file: MultipartFile, restaurantId: UUID): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            try {
                val processedImage: ByteArray = imageProcessor.processImage(file.bytes)
                val fileName: String = "${restaurantId}_${System.currentTimeMillis()}_${file.originalFilename}"
                val storedImageUrl: String = imageStorage.storeImage(processedImage, fileName)
                storedImageUrl
            } catch (e: Exception) {
                logger.error("Error processing and storing image for restaurant $restaurantId", e)
                throw ImageProcessingException("Failed to process and store image", e)
            }
        }
    }

    @Async
    override fun deleteImage(imageUrl: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                val success: Boolean = imageStorage.deleteImage(imageUrl)
                success
            } catch (e: Exception) {
                logger.error("Error deleting image: $imageUrl", e)
                throw ImageDeletionException("Failed to delete image", e)
            }
        }
    } 