import { Request, Response } from 'express'
import { Restaurant } from '../models/Restaurant'
import { Location } from '../models/Location'
import { cacheGet, cacheSet, cacheDelete } from '../services/cache'
import { logger } from '../utils/logger'

const CACHE_TTL = 3600 // 1 hour

export const createRestaurant = async (req: Request, res: Response) => {
  try {
    const { location, ...restaurantData } = req.body

    // Verify location exists
    const existingLocation = await Location.findOne({
      country: location.country,
      city: location.city,
      area: location.area
    })

    if (!existingLocation) {
      return res.status(400).json({ error: 'Invalid location' })
    }

    const restaurant = new Restaurant({
      ...restaurantData,
      location,
      createdBy: req.user._id
    })

    await restaurant.save()

    // Invalidate relevant caches
    await cacheDelete(`restaurants:${location.country}:${location.city}:${location.area}`)

    res.status(201).json(restaurant)
  } catch (error) {
    logger.error('Error creating restaurant:', error)
    res.status(500).json({ error: 'Failed to create restaurant' })
  }
}

export const updateRestaurant = async (req: Request, res: Response) => {
  try {
    const { id } = req.params
    const { location, ...updateData } = req.body

    const restaurant = await Restaurant.findById(id)
    if (!restaurant) {
      return res.status(404).json({ error: 'Restaurant not found' })
    }

    if (location) {
      // Verify new location exists
      const existingLocation = await Location.findOne({
        country: location.country,
        city: location.city,
        area: location.area
      })

      if (!existingLocation) {
        return res.status(400).json({ error: 'Invalid location' })
      }

      // Invalidate old location cache
      await cacheDelete(
        `restaurants:${restaurant.location.country}:${restaurant.location.city}:${restaurant.location.area}`
      )
    }

    Object.assign(restaurant, updateData)
    if (location) {
      restaurant.location = location
    }

    await restaurant.save()

    // Invalidate new location cache
    await cacheDelete(
      `restaurants:${restaurant.location.country}:${restaurant.location.city}:${restaurant.location.area}`
    )

    res.json(restaurant)
  } catch (error) {
    logger.error('Error updating restaurant:', error)
    res.status(500).json({ error: 'Failed to update restaurant' })
  }
}

export const getRestaurants = async (req: Request, res: Response) => {
  try {
    const { country, city, area, cuisine, priceRange, page = 1, limit = 10 } = req.query
    const filter: any = { isActive: true }

    if (country) filter['location.country'] = country
    if (city) filter['location.city'] = city
    if (area) filter['location.area'] = area
    if (cuisine) filter.cuisine = cuisine
    if (priceRange) filter.priceRange = priceRange

    const cacheKey = `restaurants:${JSON.stringify(filter)}:${page}:${limit}`
    const cachedData = await cacheGet(cacheKey)

    if (cachedData) {
      return res.json(JSON.parse(cachedData))
    }

    const skip = (Number(page) - 1) * Number(limit)
    const [restaurants, total] = await Promise.all([
      Restaurant.find(filter)
        .sort({ 'rating.average': -1 })
        .skip(skip)
        .limit(Number(limit)),
      Restaurant.countDocuments(filter)
    ])

    const data = {
      restaurants,
      total,
      page: Number(page),
      totalPages: Math.ceil(total / Number(limit))
    }

    await cacheSet(cacheKey, JSON.stringify(data), CACHE_TTL)
    res.json(data)
  } catch (error) {
    logger.error('Error fetching restaurants:', error)
    res.status(500).json({ error: 'Failed to fetch restaurants' })
  }
}

export const getRestaurantById = async (req: Request, res: Response) => {
  try {
    const { id } = req.params
    const cacheKey = `restaurant:${id}`
    const cachedData = await cacheGet(cacheKey)

    if (cachedData) {
      return res.json(JSON.parse(cachedData))
    }

    const restaurant = await Restaurant.findById(id)
    if (!restaurant) {
      return res.status(404).json({ error: 'Restaurant not found' })
    }

    await cacheSet(cacheKey, JSON.stringify(restaurant), CACHE_TTL)
    res.json(restaurant)
  } catch (error) {
    logger.error('Error fetching restaurant:', error)
    res.status(500).json({ error: 'Failed to fetch restaurant' })
  }
}

export const searchRestaurants = async (req: Request, res: Response) => {
  try {
    const { query, location } = req.query
    const filter: any = { isActive: true }

    if (location) {
      const [country, city, area] = (location as string).split(',')
      if (country) filter['location.country'] = country
      if (city) filter['location.city'] = city
      if (area) filter['location.area'] = area
    }

    if (query) {
      filter.$or = [
        { name: new RegExp(query as string, 'i') },
        { cuisine: new RegExp(query as string, 'i') },
        { description: new RegExp(query as string, 'i') }
      ]
    }

    const restaurants = await Restaurant.find(filter)
      .sort({ 'rating.average': -1 })
      .limit(50)

    res.json(restaurants)
  } catch (error) {
    logger.error('Error searching restaurants:', error)
    res.status(500).json({ error: 'Failed to search restaurants' })
  }
}

export const deleteRestaurant = async (req: Request, res: Response) => {
  try {
    const { id } = req.params
    const restaurant = await Restaurant.findById(id)
    
    if (!restaurant) {
      return res.status(404).json({ error: 'Restaurant not found' })
    }

    // Instead of deleting, mark as inactive
    restaurant.isActive = false
    await restaurant.save()

    // Invalidate caches
    await Promise.all([
      cacheDelete(`restaurant:${id}`),
      cacheDelete(
        `restaurants:${restaurant.location.country}:${restaurant.location.city}:${restaurant.location.area}`
      )
    ])

    res.json({ message: 'Restaurant deleted successfully' })
  } catch (error) {
    logger.error('Error deleting restaurant:', error)
    res.status(500).json({ error: 'Failed to delete restaurant' })
  }
}
