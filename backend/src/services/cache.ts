import Redis from 'ioredis'
import { logger } from '../utils/logger'

const redis = new Redis({
  host: process.env.REDIS_HOST || 'localhost',
  port: parseInt(process.env.REDIS_PORT || '6379'),
  password: process.env.REDIS_PASSWORD,
  retryStrategy: (times) => {
    const delay = Math.min(times * 50, 2000)
    return delay
  }
})

redis.on('error', (err) => {
  logger.error('Redis connection error:', err)
})

export const cacheGet = async (key: string) => {
  try {
    const data = await redis.get(key)
    return data ? JSON.parse(data) : null
  } catch (error) {
    logger.error('Redis get error:', error)
    return null
  }
}

export const cacheSet = async (key: string, value: any, expireSeconds = 3600) => {
  try {
    await redis.set(key, JSON.stringify(value), 'EX', expireSeconds)
  } catch (error) {
    logger.error('Redis set error:', error)
  }
}

export const cacheDelete = async (key: string) => {
  try {
    await redis.del(key)
  } catch (error) {
    logger.error('Redis delete error:', error)
  }
}

export const cacheDeletePattern = async (pattern: string) => {
  try {
    const keys = await redis.keys(pattern)
    if (keys.length > 0) {
      await redis.del(...keys)
    }
  } catch (error) {
    logger.error('Redis delete pattern error:', error)
  }
}
