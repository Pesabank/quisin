import jwt from 'jsonwebtoken'
import { User } from '../../models/User'

interface TestUser {
  isAdmin?: boolean
  email?: string
  password?: string
}

export const createTestUser = async (options: TestUser = {}) => {
  const user = new User({
    email: options.email || 'test@example.com',
    password: options.password || 'password123',
    isAdmin: options.isAdmin || false
  })
  await user.save()
  return user
}

export const getAuthToken = async (user: any) => {
  const token = jwt.sign(
    { userId: user._id, isAdmin: user.isAdmin },
    process.env.JWT_SECRET || 'test-secret',
    { expiresIn: '1h' }
  )
  return token
}
