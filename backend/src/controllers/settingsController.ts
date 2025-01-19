import { Request, Response } from 'express'
import { LoyaltySettings } from '../models/LoyaltySettings'

export const getLoyaltySettings = async (req: Request, res: Response) => {
  try {
    const settings = await LoyaltySettings.findOne()
    if (!settings) {
      // Create default settings if none exist
      const defaultSettings = new LoyaltySettings({
        enabled: false,
        pointsPerDollar: 1,
        tierThresholds: {
          silver: 1000,
          gold: 5000,
          platinum: 10000
        }
      })
      await defaultSettings.save()
      return res.json(defaultSettings)
    }
    res.json(settings)
  } catch (error) {
    console.error('Error fetching loyalty settings:', error)
    res.status(500).json({ error: 'Failed to fetch loyalty settings' })
  }
}

export const updateLoyaltySettings = async (req: Request, res: Response) => {
  try {
    const { enabled, pointsPerDollar, tierThresholds } = req.body

    const settings = await LoyaltySettings.findOne()
    if (!settings) {
      const newSettings = new LoyaltySettings({
        enabled,
        pointsPerDollar,
        tierThresholds
      })
      await newSettings.save()
      return res.json(newSettings)
    }

    settings.enabled = enabled
    if (pointsPerDollar !== undefined) settings.pointsPerDollar = pointsPerDollar
    if (tierThresholds) settings.tierThresholds = tierThresholds
    settings.updatedAt = new Date()

    await settings.save()
    res.json(settings)
  } catch (error) {
    console.error('Error updating loyalty settings:', error)
    res.status(500).json({ error: 'Failed to update loyalty settings' })
  }
}

export const toggleLoyaltyProgram = async (req: Request, res: Response) => {
  try {
    const { enabled } = req.body
    const settings = await LoyaltySettings.findOne()
    
    if (!settings) {
      return res.status(404).json({ error: 'Loyalty settings not found' })
    }

    settings.enabled = enabled
    settings.updatedAt = new Date()
    await settings.save()
    
    res.json(settings)
  } catch (error) {
    console.error('Error toggling loyalty program:', error)
    res.status(500).json({ error: 'Failed to toggle loyalty program' })
  }
}
