import { Request, Response } from 'express';
import { cache } from '../services/cache';
import { notificationService } from '../services/notification';
import { logger } from '../utils/logger';

const COOLDOWN_PERIOD = 5 * 60; // 5 minutes in seconds

export const waiterController = {
  async hailWaiter(req: Request, res: Response) {
    try {
      const { restaurantId, tableId } = req.body;
      const userId = req.user?.id;

      if (!userId || !restaurantId || !tableId) {
        return res.status(400).json({
          error: 'Missing required fields'
        });
      }

      // Check rate limit
      const rateLimitKey = `waiter_hail:${restaurantId}:${tableId}:${userId}`;
      const lastHailTime = await cache.get(rateLimitKey);

      if (lastHailTime) {
        const timeElapsed = Math.floor(Date.now() / 1000) - parseInt(lastHailTime);
        if (timeElapsed < COOLDOWN_PERIOD) {
          return res.status(429).json({
            error: 'Please wait before hailing again',
            remainingTime: COOLDOWN_PERIOD - timeElapsed
          });
        }
      }

      // Store the current timestamp
      await cache.set(rateLimitKey, Math.floor(Date.now() / 1000).toString(), COOLDOWN_PERIOD);

      // Send notification to waiters
      await notificationService.sendNotificationToRestaurant(restaurantId, {
        type: 'waiter_hail',
        title: 'Waiter Requested',
        message: `Table ${tableId} needs assistance`,
        severity: 'info',
        data: {
          tableId,
          userId
        },
        timestamp: new Date()
      });

      // Send confirmation to customer
      await notificationService.sendNotificationToUser(userId, {
        type: 'waiter_hail_confirmation',
        title: 'Waiter Called',
        message: 'A waiter will be with you shortly',
        severity: 'success',
        data: {
          tableId
        },
        timestamp: new Date()
      });

      res.json({
        success: true,
        message: 'Waiter has been notified',
        cooldownPeriod: COOLDOWN_PERIOD
      });
    } catch (error) {
      logger.error('Error hailing waiter:', error);
      res.status(500).json({
        error: 'Failed to hail waiter'
      });
    }
  },

  async getHailStatus(req: Request, res: Response) {
    try {
      const { restaurantId, tableId } = req.params;
      const userId = req.user?.id;

      if (!userId || !restaurantId || !tableId) {
        return res.status(400).json({
          error: 'Missing required fields'
        });
      }

      const rateLimitKey = `waiter_hail:${restaurantId}:${tableId}:${userId}`;
      const lastHailTime = await cache.get(rateLimitKey);

      if (!lastHailTime) {
        return res.json({
          canHail: true
        });
      }

      const timeElapsed = Math.floor(Date.now() / 1000) - parseInt(lastHailTime);
      const remainingTime = Math.max(0, COOLDOWN_PERIOD - timeElapsed);

      res.json({
        canHail: remainingTime === 0,
        remainingTime
      });
    } catch (error) {
      logger.error('Error getting hail status:', error);
      res.status(500).json({
        error: 'Failed to get hail status'
      });
    }
  }
};
