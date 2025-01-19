  fun createRestaurant(request: CreateRestaurantRequest): Restaurant {
        val chain = chainRepository.findById(UUID.fromString(request.chainId))
            .orElseThrow { ChainNotFoundException("Chain not found with id: ${request.chainId}") }

        val restaurant = Restaurant(
            name = request.name,
            description = request.description,
            chain = chain,
            address = request.address,
            contactNumber = request.contactNumber,
            email = request.email,
            adminUserId = request.adminUserId,
            createdAt = LocalDateTime.now(),
            status = RestaurantStatus.ACTIVE
        )

        return restaurantRepository.save(restaurant)
    }

    fun updateRestaurant(id: UUID, request: UpdateRestaurantRequest): Restaurant {
        val chain = chainRepository.findById(UUID.fromString(request.chainId))
            .orElseThrow { ChainNotFoundException("Chain not found with id: ${request.chainId}") }

        val restaurant = restaurantRepository.findById(id)
            .orElseThrow { RestaurantNotFoundException("Restaurant not found with id: $id") }

        restaurant.apply {
            name = request.name
            description = request.description
            this.chain = chain
            address = request.address
            contactNumber = request.contactNumber
            email = request.email
            adminUserId = request.adminUserId
            updatedAt = LocalDateTime.now()
        }

        return restaurantRepository.save(restaurant)
    } 