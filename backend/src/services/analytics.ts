import mongoose from 'mongoose';
import { Restaurant } from '../models/Restaurant';
import { Order } from '../models/Order';
import { cache } from './cache';
import { logger } from '../utils/logger';

interface SalesMetrics {
  totalSales: number;
  averageOrderValue: number;
  orderCount: number;
  topSellingItems: Array<{
    itemId: string;
    name: string;
    quantity: number;
    revenue: number;
  }>;
}

interface PerformanceMetrics {
  averagePreparationTime: number;
  averageDeliveryTime: number;
  orderCompletionRate: number;
  customerSatisfactionScore: number;
}

interface RestaurantAnalytics {
  dailyMetrics: {
    sales: SalesMetrics;
    performance: PerformanceMetrics;
  };
  weeklyMetrics: {
    sales: SalesMetrics;
    performance: PerformanceMetrics;
  };
  monthlyMetrics: {
    sales: SalesMetrics;
    performance: PerformanceMetrics;
  };
}

class AnalyticsService {
  private static CACHE_TTL = 3600; // 1 hour

  private async calculateSalesMetrics(
    restaurantId: string,
    startDate: Date,
    endDate: Date
  ): Promise<SalesMetrics> {
    try {
      const orders = await Order.find({
        restaurantId,
        createdAt: { $gte: startDate, $lte: endDate },
        status: 'completed'
      });

      const totalSales = orders.reduce((sum, order) => sum + order.totalAmount, 0);
      const orderCount = orders.length;

      // Calculate top selling items
      const itemSales = new Map<string, { quantity: number; revenue: number; name: string }>();
      orders.forEach(order => {
        order.items.forEach(item => {
          const existing = itemSales.get(item.itemId.toString()) || { quantity: 0, revenue: 0, name: item.name };
          itemSales.set(item.itemId.toString(), {
            quantity: existing.quantity + item.quantity,
            revenue: existing.revenue + (item.price * item.quantity),
            name: item.name
          });
        });
      });

      const topSellingItems = Array.from(itemSales.entries())
        .map(([itemId, data]) => ({
          itemId,
          name: data.name,
          quantity: data.quantity,
          revenue: data.revenue
        }))
        .sort((a, b) => b.revenue - a.revenue)
        .slice(0, 10);

      return {
        totalSales,
        averageOrderValue: orderCount > 0 ? totalSales / orderCount : 0,
        orderCount,
        topSellingItems
      };
    } catch (error) {
      logger.error('Error calculating sales metrics:', error);
      throw error;
    }
  }

  private async calculatePerformanceMetrics(
    restaurantId: string,
    startDate: Date,
    endDate: Date
  ): Promise<PerformanceMetrics> {
    try {
      const orders = await Order.find({
        restaurantId,
        createdAt: { $gte: startDate, $lte: endDate }
      });

      const completedOrders = orders.filter(order => order.status === 'completed');
      const totalOrders = orders.length;

      let totalPrepTime = 0;
      let totalDeliveryTime = 0;
      let totalRating = 0;
      let ratedOrders = 0;

      completedOrders.forEach(order => {
        if (order.preparationTime) {
          totalPrepTime += order.preparationTime;
        }
        if (order.deliveryTime) {
          totalDeliveryTime += order.deliveryTime;
        }
        if (order.rating) {
          totalRating += order.rating;
          ratedOrders++;
        }
      });

      return {
        averagePreparationTime: completedOrders.length > 0 ? totalPrepTime / completedOrders.length : 0,
        averageDeliveryTime: completedOrders.length > 0 ? totalDeliveryTime / completedOrders.length : 0,
        orderCompletionRate: totalOrders > 0 ? (completedOrders.length / totalOrders) * 100 : 0,
        customerSatisfactionScore: ratedOrders > 0 ? (totalRating / ratedOrders) * 5 : 0
      };
    } catch (error) {
      logger.error('Error calculating performance metrics:', error);
      throw error;
    }
  }

  public async getRestaurantAnalytics(restaurantId: string): Promise<RestaurantAnalytics> {
    const cacheKey = `analytics:${restaurantId}`;
    const cachedData = await cache.get(cacheKey);

    if (cachedData) {
      return JSON.parse(cachedData);
    }

    const now = new Date();
    const dayStart = new Date(now.setHours(0, 0, 0, 0));
    const weekStart = new Date(now.setDate(now.getDate() - 7));
    const monthStart = new Date(now.setDate(now.getDate() - 30));

    const [dailySales, dailyPerf, weeklySales, weeklyPerf, monthlySales, monthlyPerf] = await Promise.all([
      this.calculateSalesMetrics(restaurantId, dayStart, now),
      this.calculatePerformanceMetrics(restaurantId, dayStart, now),
      this.calculateSalesMetrics(restaurantId, weekStart, now),
      this.calculatePerformanceMetrics(restaurantId, weekStart, now),
      this.calculateSalesMetrics(restaurantId, monthStart, now),
      this.calculatePerformanceMetrics(restaurantId, monthStart, now)
    ]);

    const analytics: RestaurantAnalytics = {
      dailyMetrics: {
        sales: dailySales,
        performance: dailyPerf
      },
      weeklyMetrics: {
        sales: weeklySales,
        performance: weeklyPerf
      },
      monthlyMetrics: {
        sales: monthlySales,
        performance: monthlyPerf
      }
    };

    await cache.set(cacheKey, JSON.stringify(analytics), AnalyticsService.CACHE_TTL);
    return analytics;
  }

  public async generateAnalyticsReport(restaurantId: string): Promise<Buffer> {
    const analytics = await this.getRestaurantAnalytics(restaurantId);
    const restaurant = await Restaurant.findById(restaurantId);

    if (!restaurant) {
      throw new Error('Restaurant not found');
    }

    // Generate PDF report using a PDF library (implementation details omitted)
    // This would typically use a library like PDFKit to generate a formatted PDF report
    
    return Buffer.from('PDF Report'); // Placeholder
  }
}

export const analyticsService = new AnalyticsService();
