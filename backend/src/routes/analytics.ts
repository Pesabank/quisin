import { Router } from 'express'
import { getRevenueStats, getDashboardStats, exportAnalytics } from '../controllers/analyticsController'
import { authenticate } from '../middleware/auth'
import { validateDateRange } from '../middleware/validation'

const router = Router()

router.use(authenticate)

// Get revenue statistics
router.get('/revenue', validateDateRange, getRevenueStats)

// Get dashboard statistics
router.get('/dashboard', getDashboardStats)

// Export analytics data
router.get('/export', validateDateRange, exportAnalytics)

export default router
