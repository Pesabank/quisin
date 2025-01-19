import request from 'supertest'
import { app } from '../app'
import { connectTestDB, closeTestDB, clearTestDB } from './helpers/db'
import { createTestUser, getAuthToken } from './helpers/auth'
import { Order } from '../models/Order'
import { addDays, subDays } from 'date-fns'

describe('Analytics API', () => {
  let authToken: string

  beforeAll(async () => {
    await connectTestDB()
    const user = await createTestUser({ role: 'admin' })
    authToken = getAuthToken(user)
  })

  afterAll(async () => {
    await clearTestDB()
    await closeTestDB()
  })

  beforeEach(async () => {
    await clearTestDB()
  })

  describe('GET /api/analytics/revenue', () => {
    it('should return revenue statistics', async () => {
      // Create test orders
      const today = new Date()
      const orders = [
        {
          total: 100,
          status: 'completed',
          createdAt: today,
          items: [{ id: '1', quantity: 1, price: 100 }]
        },
        {
          total: 200,
          status: 'completed',
          createdAt: subDays(today, 1),
          items: [{ id: '2', quantity: 2, price: 100 }]
        }
      ]

      await Order.insertMany(orders)

      const response = await request(app)
        .get('/api/analytics/revenue')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)

      expect(response.body).toHaveProperty('labels')
      expect(response.body).toHaveProperty('revenue')
      expect(response.body).toHaveProperty('orders')
      expect(response.body.revenue).toHaveLength(2)
    })

    it('should return 401 without auth token', async () => {
      await request(app)
        .get('/api/analytics/revenue')
        .expect(401)
    })
  })

  describe('GET /api/analytics/dashboard', () => {
    it('should return dashboard statistics', async () => {
      // Create test orders
      const today = new Date()
      const orders = [
        {
          total: 100,
          status: 'completed',
          createdAt: today,
          items: [{ id: '1', quantity: 1, price: 100 }]
        },
        {
          total: 200,
          status: 'completed',
          createdAt: subDays(today, 1),
          items: [{ id: '2', quantity: 2, price: 100 }]
        }
      ]

      await Order.insertMany(orders)

      const response = await request(app)
        .get('/api/analytics/dashboard')
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)

      expect(response.body).toHaveProperty('revenue')
      expect(response.body).toHaveProperty('orders')
      expect(response.body).toHaveProperty('averageOrder')
    })
  })

  describe('GET /api/analytics/export', () => {
    it('should export data in CSV format', async () => {
      // Create test orders
      const today = new Date()
      const orders = [
        {
          total: 100,
          status: 'completed',
          createdAt: today,
          items: [{ id: '1', quantity: 1, price: 100 }]
        }
      ]

      await Order.insertMany(orders)

      const response = await request(app)
        .get('/api/analytics/export')
        .query({
          startDate: subDays(today, 7).toISOString(),
          endDate: today.toISOString(),
          format: 'csv'
        })
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)
        .expect('Content-Type', /text\/csv/)

      expect(response.text).toContain('orderId,date,total,items,status')
    })

    it('should export data in JSON format', async () => {
      const today = new Date()
      const orders = [
        {
          total: 100,
          status: 'completed',
          createdAt: today,
          items: [{ id: '1', quantity: 1, price: 100 }]
        }
      ]

      await Order.insertMany(orders)

      const response = await request(app)
        .get('/api/analytics/export')
        .query({
          startDate: subDays(today, 7).toISOString(),
          endDate: today.toISOString(),
          format: 'json'
        })
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)
        .expect('Content-Type', /application\/json/)

      expect(Array.isArray(response.body)).toBeTruthy()
      expect(response.body[0]).toHaveProperty('orderId')
      expect(response.body[0]).toHaveProperty('total')
    })

    it('should validate date range', async () => {
      const today = new Date()
      
      await request(app)
        .get('/api/analytics/export')
        .query({
          startDate: addDays(today, 1).toISOString(),
          endDate: today.toISOString(),
          format: 'csv'
        })
        .set('Authorization', `Bearer ${authToken}`)
        .expect(400)
    })
  })
})
