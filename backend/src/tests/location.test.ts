import request from 'supertest'
import { app } from '../app'
import { connectTestDB, closeTestDB, clearTestDB } from './helpers/db'
import { createTestUser, getAuthToken } from './helpers/auth'
import { Location } from '../models/Location'

describe('Location API', () => {
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

  describe('GET /api/locations/countries', () => {
    it('should return list of countries', async () => {
      await Location.create([
        { country: 'USA', city: 'New York', area: 'Manhattan', latitude: 40.7128, longitude: -74.0060 },
        { country: 'UK', city: 'London', area: 'Westminster', latitude: 51.5074, longitude: -0.1278 }
      ])

      const response = await request(app)
        .get('/api/locations/countries')
        .expect(200)

      expect(response.body).toContain('USA')
      expect(response.body).toContain('UK')
    })
  })

  describe('GET /api/locations/cities/:country', () => {
    it('should return cities for a country', async () => {
      await Location.create([
        { country: 'USA', city: 'New York', area: 'Manhattan', latitude: 40.7128, longitude: -74.0060 },
        { country: 'USA', city: 'Los Angeles', area: 'Downtown', latitude: 34.0522, longitude: -118.2437 }
      ])

      const response = await request(app)
        .get('/api/locations/cities/USA')
        .expect(200)

      expect(response.body).toContain('New York')
      expect(response.body).toContain('Los Angeles')
    })
  })

  describe('POST /api/locations', () => {
    it('should create a new location', async () => {
      const location = {
        country: 'Canada',
        city: 'Toronto',
        area: 'Downtown',
        latitude: 43.6532,
        longitude: -79.3832
      }

      const response = await request(app)
        .post('/api/locations')
        .set('Authorization', `Bearer ${authToken}`)
        .send(location)
        .expect(201)

      expect(response.body.country).toBe(location.country)
      expect(response.body.city).toBe(location.city)
      expect(response.body.area).toBe(location.area)
    })

    it('should prevent duplicate locations', async () => {
      const location = {
        country: 'Canada',
        city: 'Toronto',
        area: 'Downtown',
        latitude: 43.6532,
        longitude: -79.3832
      }

      await Location.create(location)

      await request(app)
        .post('/api/locations')
        .set('Authorization', `Bearer ${authToken}`)
        .send(location)
        .expect(400)
    })
  })

  describe('PUT /api/locations/:id', () => {
    it('should update a location', async () => {
      const location = await Location.create({
        country: 'Canada',
        city: 'Toronto',
        area: 'Downtown',
        latitude: 43.6532,
        longitude: -79.3832
      })

      const update = {
        area: 'Midtown',
        latitude: 43.7000
      }

      const response = await request(app)
        .put(`/api/locations/${location._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(update)
        .expect(200)

      expect(response.body.area).toBe(update.area)
      expect(response.body.latitude).toBe(update.latitude)
    })
  })

  describe('DELETE /api/locations/:id', () => {
    it('should delete a location', async () => {
      const location = await Location.create({
        country: 'Canada',
        city: 'Toronto',
        area: 'Downtown',
        latitude: 43.6532,
        longitude: -79.3832
      })

      await request(app)
        .delete(`/api/locations/${location._id}`)
        .set('Authorization', `Bearer ${authToken}`)
        .expect(200)

      const deleted = await Location.findById(location._id)
      expect(deleted).toBeNull()
    })
  })

  describe('GET /api/locations/search', () => {
    it('should search locations', async () => {
      await Location.create([
        { country: 'USA', city: 'New York', area: 'Manhattan', latitude: 40.7128, longitude: -74.0060 },
        { country: 'USA', city: 'Los Angeles', area: 'Downtown', latitude: 34.0522, longitude: -118.2437 }
      ])

      const response = await request(app)
        .get('/api/locations/search')
        .query({ query: 'New York' })
        .expect(200)

      expect(response.body).toHaveLength(1)
      expect(response.body[0].city).toBe('New York')
    })
  })
})
