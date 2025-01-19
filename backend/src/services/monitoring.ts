import { Request, Response, NextFunction } from 'express'
import { logger } from '../utils/logger'
import { performance } from 'perf_hooks'
import { cacheSet, cacheGet } from './cache'

interface MetricData {
  count: number
  totalTime: number
  avgTime: number
  p95Time: number
  errors: number
}

class MonitoringService {
  private static instance: MonitoringService
  private metrics: Map<string, MetricData>
  private readonly CACHE_TTL = 3600 // 1 hour

  private constructor() {
    this.metrics = new Map()
  }

  static getInstance(): MonitoringService {
    if (!MonitoringService.instance) {
      MonitoringService.instance = new MonitoringService()
    }
    return MonitoringService.instance
  }

  startRequest(req: Request): void {
    req.locals = {
      ...req.locals,
      startTime: performance.now()
    }
  }

  endRequest(req: Request, res: Response): void {
    const endTime = performance.now()
    const duration = endTime - (req.locals?.startTime || endTime)
    const path = req.route?.path || req.path
    const method = req.method
    const statusCode = res.statusCode
    const key = `${method}:${path}`

    this.updateMetrics(key, duration, statusCode >= 400)
  }

  private async updateMetrics(key: string, duration: number, isError: boolean): Promise<void> {
    const cacheKey = `metrics:${key}`
    const cachedData = await cacheGet(cacheKey)
    let metrics: MetricData

    if (cachedData) {
      metrics = JSON.parse(cachedData)
    } else {
      metrics = {
        count: 0,
        totalTime: 0,
        avgTime: 0,
        p95Time: 0,
        errors: 0
      }
    }

    metrics.count++
    metrics.totalTime += duration
    metrics.avgTime = metrics.totalTime / metrics.count
    if (isError) metrics.errors++

    // Update p95 time
    const p95Index = Math.floor(metrics.count * 0.95)
    if (duration > metrics.p95Time || metrics.count <= p95Index) {
      metrics.p95Time = duration
    }

    await cacheSet(cacheKey, JSON.stringify(metrics), this.CACHE_TTL)
  }

  async getMetrics(): Promise<{ [key: string]: MetricData }> {
    const metrics: { [key: string]: MetricData } = {}
    const keys = await this.getAllMetricKeys()

    for (const key of keys) {
      const data = await cacheGet(key)
      if (data) {
        metrics[key.replace('metrics:', '')] = JSON.parse(data)
      }
    }

    return metrics
  }

  private async getAllMetricKeys(): Promise<string[]> {
    // This would need to be implemented based on your Redis client
    // For now, we'll return a placeholder
    return []
  }

  logError(error: Error, req: Request): void {
    logger.error('Application error:', {
      error: error.message,
      stack: error.stack,
      path: req.path,
      method: req.method,
      userId: req.user?._id,
      timestamp: new Date().toISOString()
    })
  }
}

// Middleware for request monitoring
export const monitorRequest = (req: Request, res: Response, next: NextFunction): void => {
  const monitoring = MonitoringService.getInstance()
  monitoring.startRequest(req)

  res.on('finish', () => {
    monitoring.endRequest(req, res)
  })

  next()
}

export const monitoring = MonitoringService.getInstance()
