import request from 'supertest'
import { app } from '../app'
import { Inventory } from '../models/Inventory'
import { connectDB, closeDB, clearDB } from './helpers/db'
import { createTestUser, getAuthToken } from './helpers/auth'

describe('Inventory API', () => {
  let authToken: string

  beforeAll(async () => {
    await connectDB()
    const user = await createTestUser({ isAdmin: true })
    authToken = await getAuthToken(user)
  })

  afterAll(async () => {
    await clearDB()
    await closeDB()
  })

  beforeEach(async () => {
    await Inventory.deleteMany({})
  })

  describe('GET /api/inventory', () => {
    it('returns paginated inventory items', async () => {
      // Create 15 test items
      const items = Array.from({ length: 15 }, (_, i) => ({
        name: `Test Item ${i + 1}`,
        category: `Category ${Math.floor(i / 5) + 1}`,
        quantity: 10 * (i + 1),
        minQuantity: 5 * (i + 1),
        unit: 'pcs',
        unitPrice: 9.99 * (i + 1),
        supplier: `Supplier ${Math.floor(i / 3) + 1}`
      }))

      await Inventory.insertMany(items)

      const response = await request(app)
        .get('/api/inventory?page=1&limit=10')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)

      expect(response.body.items).toHaveLength(10)
      expect(response.body.total).toBe(15)
      expect(response.body.totalPages).toBe(2)
    })

    it('filters items by category', async () => {
      await Inventory.insertMany([
        { name: 'Item 1', category: 'Food' },
        { name: 'Item 2', category: 'Beverage' }
      ])

      const response = await request(app)
        .get('/api/inventory?category=Food')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)

      expect(response.body.items).toHaveLength(1)
      expect(response.body.items[0].category).toBe('Food')
    })

    it('searches items by name or supplier', async () => {
      await Inventory.insertMany([
        { name: 'Test Item', supplier: 'Supplier A' },
        { name: 'Another Item', supplier: 'Test Supplier' }
      ])

      const response = await request(app)
        .get('/api/inventory?search=test')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)

      expect(response.body.items).toHaveLength(2)
    })
  })

  describe('POST /api/inventory', () => {
    it('creates a new inventory item', async () => {
      const newItem = {
        name: 'New Item',
        category: 'Test Category',
        quantity: 100,
        minQuantity: 10,
        unit: 'pcs',
        unitPrice: 9.99,
        supplier: 'Test Supplier'
      }

      const response = await request(app)
        .post('/api/inventory')
        .set('Authorization', `Bearer ${authToken}`)
        .send(newItem)
        .expect(201)

      expect(response.body.name).toBe(newItem.name)
      expect(response.body.quantity).toBe(newItem.quantity)

      const savedItem = await Inventory.findById(response.body._id)
      expect(savedItem).toBeTruthy()
    })

    it('validates required fields', async () => {
      const invalidItem = {
        name: 'Invalid Item'
        // Missing required fields
      }

      await request(app)
        .post('/api/inventory')
        .set('Authorization', `Bearer ${authToken}`)
        .send(invalidItem)
        .expect(400)
    })
  })

  describe('PUT /api/inventory/:id', () => {
    it('updates an existing inventory item', async () => {
      const item = await Inventory.create({
        name: 'Test Item',
        category: 'Test Category',
        quantity: 100,
        minQuantity: 10,
        unit: 'pcs',
        unitPrice: 9.99,
        supplier: 'Test Supplier'
      })

      const updates = {
        quantity: 150,
        unitPrice: 12.99
      }

      const response = await request(app)
        .put(`/api/inventory/${item._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(updates)
        .expect(200)

      expect(response.body.quantity).toBe(updates.quantity)
      expect(response.body.unitPrice).toBe(updates.unitPrice)
    })
  })

  describe('POST /api/inventory/:id/adjust', () => {
    it('adjusts inventory quantity', async () => {
      const item = await Inventory.create({
        name: 'Test Item',
        category: 'Test Category',
        quantity: 100,
        minQuantity: 10,
        unit: 'pcs',
        unitPrice: 9.99,
        supplier: 'Test Supplier'
      })

      const adjustment = {
        adjustment: -10,
        reason: 'Stock used in order'
      }

      const response = await request(app)
        .post(`/api/inventory/${item._id}/adjust`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(adjustment)
        .expect(200)

      expect(response.body.quantity).toBe(90)
    })

    it('prevents negative inventory', async () => {
      const item = await Inventory.create({
        name: 'Test Item',
        category: 'Test Category',
        quantity: 10,
        minQuantity: 5,
        unit: 'pcs',
        unitPrice: 9.99,
        supplier: 'Test Supplier'
      })

      const adjustment = {
        adjustment: -20,
        reason: 'Invalid adjustment'
      }

      await request(app)
        .post(`/api/inventory/${item._id}/adjust`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(adjustment)
        .expect(400)
    })
  })
})
