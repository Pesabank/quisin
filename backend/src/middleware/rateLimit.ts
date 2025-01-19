import rateLimit from 'express-rate-limit'
import RedisStore from 'rate-limit-redis'
import Redis from 'ioredis'
import { logger } from '../utils/logger'

const redis = new Redis({
  host: process.env.REDIS_HOST || 'localhost',
  port: parseInt(process.env.REDIS_PORT || '6379'),
  password: process.env.REDIS_PASSWORD,
})

redis.on('error', (err) => {
  logger.error('Redis rate limit error:', err)
})

// General API rate limit
export const apiLimiter = rateLimit({
  store: new RedisStore({
    sendCommand: (...args: string[]) => redis.call(...args),
  }),
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
  message: 'Too many requests from this IP, please try again later',
  standardHeaders: true,
  legacyHeaders: false,
})

// Analytics endpoint specific limiter
export const analyticsLimiter = rateLimit({
  store: new RedisStore({
    sendCommand: (...args: string[]) => redis.call(...args),
  }),
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 50, // Limit each IP to 50 requests per hour
  message: 'Analytics request limit exceeded, please try again later',
  standardHeaders: true,
  legacyHeaders: false,
})

// Export endpoint specific limiter
export const exportLimiter = rateLimit({
  store: new RedisStore({
    sendCommand: (...args: string[]) => redis.call(...args),
  }),
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 10, // Limit each IP to 10 export requests per hour
  message: 'Export request limit exceeded, please try again later',
  standardHeaders: true,
  legacyHeaders: false,
})
