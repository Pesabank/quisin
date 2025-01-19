import { Request, Response } from 'express'
import { Location } from '../models/Location'
import { cacheGet, cacheSet } from '../services/cache'
import { logger } from '../utils/logger'

const CACHE_TTL = 3600 // 1 hour

export const getCountries = async (req: Response, res: Response) => {
  try {
    const cacheKey = 'countries:list'
    const cachedCountries = await cacheGet(cacheKey)

    if (cachedCountries) {
      return res.json(JSON.parse(cachedCountries))
    }

    const countries = await Location.distinct('country')
    await cacheSet(cacheKey, JSON.stringify(countries), CACHE_TTL)
    res.json(countries)
  } catch (error) {
    logger.error('Error fetching countries:', error)
    res.status(500).json({ error: 'Failed to fetch countries' })
  }
}

export const getCitiesByCountry = async (req: Request, res: Response) => {
  try {
    const { country } = req.params
    const cacheKey = `cities:${country}`
    const cachedCities = await cacheGet(cacheKey)

    if (cachedCities) {
      return res.json(JSON.parse(cachedCities))
    }

    const cities = await Location.distinct('city', { country })
    await cacheSet(cacheKey, JSON.stringify(cities), CACHE_TTL)
    res.json(cities)
  } catch (error) {
    logger.error('Error fetching cities:', error)
    res.status(500).json({ error: 'Failed to fetch cities' })
  }
}

export const getAreasByCity = async (req: Request, res: Response) => {
  try {
    const { country, city } = req.params
    const cacheKey = `areas:${country}:${city}`
    const cachedAreas = await cacheGet(cacheKey)

    if (cachedAreas) {
      return res.json(JSON.parse(cachedAreas))
    }

    const areas = await Location.distinct('area', { country, city })
    await cacheSet(cacheKey, JSON.stringify(areas), CACHE_TTL)
    res.json(areas)
  } catch (error) {
    logger.error('Error fetching areas:', error)
    res.status(500).json({ error: 'Failed to fetch areas' })
  }
}

export const createLocation = async (req: Request, res: Response) => {
  try {
    const { country, city, area, latitude, longitude } = req.body

    const existingLocation = await Location.findOne({ country, city, area })
    if (existingLocation) {
      return res.status(400).json({ error: 'Location already exists' })
    }

    const location = new Location({
      country,
      city,
      area,
      latitude,
      longitude
    })

    await location.save()
    
    // Invalidate relevant caches
    await Promise.all([
      cacheDelete('countries:list'),
      cacheDelete(`cities:${country}`),
      cacheDelete(`areas:${country}:${city}`)
    ])

    res.status(201).json(location)
  } catch (error) {
    logger.error('Error creating location:', error)
    res.status(500).json({ error: 'Failed to create location' })
  }
}

export const updateLocation = async (req: Request, res: Response) => {
  try {
    const { id } = req.params
    const { country, city, area, latitude, longitude, isActive } = req.body

    const location = await Location.findById(id)
    if (!location) {
      return res.status(404).json({ error: 'Location not found' })
    }

    const oldCountry = location.country
    const oldCity = location.city

    location.country = country || location.country
    location.city = city || location.city
    location.area = area || location.area
    location.latitude = latitude || location.latitude
    location.longitude = longitude || location.longitude
    location.isActive = isActive !== undefined ? isActive : location.isActive

    await location.save()

    // Invalidate relevant caches
    await Promise.all([
      cacheDelete('countries:list'),
      cacheDelete(`cities:${oldCountry}`),
      cacheDelete(`cities:${country}`),
      cacheDelete(`areas:${oldCountry}:${oldCity}`),
      cacheDelete(`areas:${country}:${city}`)
    ])

    res.json(location)
  } catch (error) {
    logger.error('Error updating location:', error)
    res.status(500).json({ error: 'Failed to update location' })
  }
}

export const deleteLocation = async (req: Request, res: Response) => {
  try {
    const { id } = req.params
    const location = await Location.findById(id)
    
    if (!location) {
      return res.status(404).json({ error: 'Location not found' })
    }

    const { country, city } = location

    await location.deleteOne()

    // Invalidate relevant caches
    await Promise.all([
      cacheDelete('countries:list'),
      cacheDelete(`cities:${country}`),
      cacheDelete(`areas:${country}:${city}`)
    ])

    res.json({ message: 'Location deleted successfully' })
  } catch (error) {
    logger.error('Error deleting location:', error)
    res.status(500).json({ error: 'Failed to delete location' })
  }
}

export const searchLocations = async (req: Request, res: Response) => {
  try {
    const { query, country, city } = req.query
    const filter: any = {}

    if (country) filter.country = country
    if (city) filter.city = city
    if (query) {
      filter.$or = [
        { area: new RegExp(query as string, 'i') },
        { city: new RegExp(query as string, 'i') },
        { country: new RegExp(query as string, 'i') }
      ]
    }

    const locations = await Location.find(filter)
      .sort({ country: 1, city: 1, area: 1 })
      .limit(50)

    res.json(locations)
  } catch (error) {
    logger.error('Error searching locations:', error)
    res.status(500).json({ error: 'Failed to search locations' })
  }
}
