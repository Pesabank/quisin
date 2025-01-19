import { Request, Response, NextFunction } from 'express'
import { isValid, parseISO } from 'date-fns'

export const validateDateRange = (req: Request, res: Response, next: NextFunction) => {
  const { startDate, endDate } = req.query

  if (startDate && !isValid(parseISO(startDate as string))) {
    return res.status(400).json({ error: 'Invalid start date format' })
  }

  if (endDate && !isValid(parseISO(endDate as string))) {
    return res.status(400).json({ error: 'Invalid end date format' })
  }

  if (startDate && endDate && parseISO(startDate as string) > parseISO(endDate as string)) {
    return res.status(400).json({ error: 'Start date must be before end date' })
  }

  next()
}

export const validateRestaurant = (req: Request, res: Response, next: NextFunction) => {
  const { name, description, location, cuisine, priceRange, openingHours } = req.body

  if (!name || typeof name !== 'string' || name.trim().length < 2) {
    return res.status(400).json({ error: 'Invalid restaurant name' })
  }

  if (!description || typeof description !== 'string' || description.trim().length < 10) {
    return res.status(400).json({ error: 'Invalid description' })
  }

  if (!location || !location.country || !location.city || !location.area || !location.address) {
    return res.status(400).json({ error: 'Invalid location details' })
  }

  if (!Array.isArray(cuisine) || cuisine.length === 0) {
    return res.status(400).json({ error: 'At least one cuisine type is required' })
  }

  if (!['$', '$$', '$$$', '$$$$'].includes(priceRange)) {
    return res.status(400).json({ error: 'Invalid price range' })
  }

  if (openingHours) {
    const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday']
    for (const day of days) {
      if (openingHours[day]) {
        const { open, close } = openingHours[day]
        if (!isValidTimeFormat(open) || !isValidTimeFormat(close)) {
          return res.status(400).json({ error: `Invalid opening hours for ${day}` })
        }
      }
    }
  }

  next()
}

const isValidTimeFormat = (time: string): boolean => {
  const timeRegex = /^([01]?[0-9]|2[0-3]):[0-5][0-9]$/
  return timeRegex.test(time)
}
