import { Request, Response } from 'express'
import { Order } from '../models/Order'
import { cacheGet, cacheSet } from '../services/cache'
import { logger } from '../utils/logger'
import { startOfDay, endOfDay, subDays, format } from 'date-fns'
import { Parser } from 'json2csv'
import { excelExportService } from '../services/excelExport'

const CACHE_TTL = 3600 // 1 hour in seconds

export const getRevenueStats = async (req: Request, res: Response) => {
  try {
    const { period = 'daily', startDate, endDate } = req.query
    const cacheKey = `revenue:${period}:${startDate}:${endDate}`

    // Try to get from cache first
    const cachedData = await cacheGet(cacheKey)
    if (cachedData) {
      return res.json(cachedData)
    }

    const start = startDate ? new Date(startDate as string) : subDays(new Date(), 30)
    const end = endDate ? new Date(endDate as string) : new Date()

    const pipeline = [
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: 'completed'
        }
      },
      {
        $group: {
          _id: {
            $dateToString: {
              format: '%Y-%m-%d',
              date: '$createdAt'
            }
          },
          revenue: { $sum: '$total' },
          orders: { $sum: 1 }
        }
      },
      { $sort: { '_id': 1 } }
    ]

    const results = await Order.aggregate(pipeline)
    const data = {
      labels: results.map(r => r._id),
      revenue: results.map(r => r.revenue),
      orders: results.map(r => r.orders)
    }

    // Cache the results
    await cacheSet(cacheKey, data, CACHE_TTL)
    res.json(data)
  } catch (error) {
    logger.error('Error fetching revenue stats:', error)
    res.status(500).json({ error: 'Failed to fetch revenue statistics' })
  }
}

export const getDashboardStats = async (req: Request, res: Response) => {
  try {
    const cacheKey = 'dashboard:stats'
    const cachedStats = await cacheGet(cacheKey)
    if (cachedStats) {
      return res.json(cachedStats)
    }

    const today = new Date()
    const yesterday = subDays(today, 1)

    const [todayStats, yesterdayStats] = await Promise.all([
      getDateStats(today),
      getDateStats(yesterday)
    ])

    const stats = {
      revenue: {
        value: todayStats.revenue,
        change: calculateChange(todayStats.revenue, yesterdayStats.revenue)
      },
      orders: {
        value: todayStats.orders,
        change: calculateChange(todayStats.orders, yesterdayStats.orders)
      },
      averageOrder: {
        value: todayStats.orders > 0 ? todayStats.revenue / todayStats.orders : 0,
        change: calculateChange(
          todayStats.orders > 0 ? todayStats.revenue / todayStats.orders : 0,
          yesterdayStats.orders > 0 ? yesterdayStats.revenue / yesterdayStats.orders : 0
        )
      }
    }

    await cacheSet(cacheKey, stats, 300) // Cache for 5 minutes
    res.json(stats)
  } catch (error) {
    logger.error('Error fetching dashboard stats:', error)
    res.status(500).json({ error: 'Failed to fetch dashboard statistics' })
  }
}

export const exportAnalytics = async (req: Request, res: Response) => {
  try {
    const { startDate, endDate, format = 'csv' } = req.query
    const start = startDate ? new Date(startDate as string) : subDays(new Date(), 30)
    const end = endDate ? new Date(endDate as string) : new Date()

    const orders = await Order.find({
      createdAt: { $gte: start, $lte: end },
      status: 'completed'
    }).lean()

    const data = orders.map(order => ({
      orderId: order._id,
      date: format(new Date(order.createdAt), 'yyyy-MM-dd'),
      total: order.total,
      items: order.items.length,
      status: order.status
    }))

    if (format === 'json') {
      res.json(data)
    } else if (format === 'excel') {
      const buffer = await excelExportService.generateExcelReport(data)
      res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
      res.setHeader('Content-Disposition', 'attachment; filename=analytics-report.xlsx')
      return res.send(buffer)
    } else {
      const parser = new Parser({
        fields: ['orderId', 'date', 'total', 'items', 'status']
      })
      const csv = parser.parse(data)
      
      res.header('Content-Type', 'text/csv')
      res.attachment(`analytics_${start.toISOString().split('T')[0]}_${end.toISOString().split('T')[0]}.csv`)
      res.send(csv)
    }
  } catch (error) {
    logger.error('Error exporting analytics:', error)
    res.status(500).json({ error: 'Failed to export analytics data' })
  }
}

const getDateStats = async (date: Date) => {
  const start = startOfDay(date)
  const end = endOfDay(date)

  const result = await Order.aggregate([
    {
      $match: {
        createdAt: { $gte: start, $lte: end },
        status: 'completed'
      }
    },
    {
      $group: {
        _id: null,
        revenue: { $sum: '$total' },
        orders: { $sum: 1 }
      }
    }
  ])

  return result[0] || { revenue: 0, orders: 0 }
}

const calculateChange = (current: number, previous: number): number => {
  if (previous === 0) return 100
  return ((current - previous) / previous) * 100
}
