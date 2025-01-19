import { Router } from 'express'
import {
  getCountries,
  getCitiesByCountry,
  getAreasByCity,
  createLocation,
  updateLocation,
  deleteLocation,
  searchLocations
} from '../controllers/locationController'
import { authenticate } from '../middleware/auth'
import { authorize } from '../middleware/authorize'
import { validateLocation } from '../middleware/validation'

const router = Router()

// Public routes
router.get('/countries', getCountries)
router.get('/cities/:country', getCitiesByCountry)
router.get('/areas/:country/:city', getAreasByCity)
router.get('/search', searchLocations)

// Protected routes (admin only)
router.use(authenticate)
router.use(authorize(['admin', 'superadmin']))

router.post('/', validateLocation, createLocation)
router.put('/:id', validateLocation, updateLocation)
router.delete('/:id', deleteLocation)

export default router
