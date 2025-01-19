import express from 'express'
import { getLoyaltySettings, updateLoyaltySettings, toggleLoyaltyProgram } from '../controllers/settingsController'
import { isAdmin } from '../middleware/auth'

const router = express.Router()

// Loyalty program settings routes
router.get('/loyalty', getLoyaltySettings)
router.put('/loyalty', isAdmin, updateLoyaltySettings)
router.post('/loyalty/toggle', isAdmin, toggleLoyaltyProgram)

export default router
