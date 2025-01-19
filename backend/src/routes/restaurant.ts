import { Router } from 'express'
import {
  createRestaurant,
  updateRestaurant,
  getRestaurants,
  getRestaurantById,
  searchRestaurants,
  deleteRestaurant
} from '../controllers/restaurantController'
import { authenticate } from '../middleware/auth'
import { authorize } from '../middleware/authorize'
import { validateRestaurant } from '../middleware/validation'

const router = Router()

// Public routes
router.get('/', getRestaurants)
router.get('/search', searchRestaurants)
router.get('/:id', getRestaurantById)

// Protected routes (admin/superadmin only)
router.use(authenticate)
router.use(authorize(['admin', 'superadmin']))

router.post('/', validateRestaurant, createRestaurant)
router.put('/:id', validateRestaurant, updateRestaurant)
router.delete('/:id', deleteRestaurant)

export default router
