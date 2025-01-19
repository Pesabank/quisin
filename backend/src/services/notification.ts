import nodemailer from 'nodemailer'
import { logger } from '../utils/logger'
import { cacheGet, cacheSet } from './cache'
import { WebSocket } from 'ws';
import { Restaurant } from '../models/Restaurant';
import { User } from '../models/User';
import { analyticsService } from './analytics';

interface EmailOptions {
  to: string
  subject: string
  text: string
  html?: string
}

interface NotificationTemplate {
  subject: string
  text: string
  html: string
}

interface NotificationPayload {
  type: string;
  title: string;
  message: string;
  severity: 'info' | 'warning' | 'error' | 'success';
  data?: any;
  timestamp: Date;
}

interface AnalyticsAlert {
  metric: string;
  threshold: number;
  condition: 'above' | 'below';
  message: string;
}

interface ContactNotificationData {
  ticketId: number;
  name: string;
  email: string;
  subject: string;
  message: string;
  type: 'QUISIN' | 'RESTAURANT';
  adminEmail: string;
  restaurantName?: string;
}

interface TicketStatusNotificationData {
  ticketId: number;
  status: string;
  assignedTo: string;
  customerEmail: string;
}

const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST,
  port: parseInt(process.env.SMTP_PORT || '587'),
  secure: process.env.SMTP_SECURE === 'true',
  auth: {
    user: process.env.SMTP_USER,
    pass: process.env.SMTP_PASS,
  },
})

export const templates = {
  pointsExpiring: (points: number, daysLeft: number): NotificationTemplate => ({
    subject: 'Your Loyalty Points are Expiring Soon!',
    text: `You have ${points} points expiring in ${daysLeft} days. Visit us to redeem them!`,
    html: `
      <h2>Your Loyalty Points are Expiring Soon!</h2>
      <p>You have <strong>${points} points</strong> that will expire in ${daysLeft} days.</p>
      <p>Visit us soon to redeem your points for great rewards!</p>
    `,
  }),
  
  pointsEarned: (points: number, total: number): NotificationTemplate => ({
    subject: 'You Earned New Loyalty Points!',
    text: `You just earned ${points} points! Your new total is ${total} points.`,
    html: `
      <h2>You Earned New Loyalty Points!</h2>
      <p>You just earned <strong>${points} points</strong>!</p>
      <p>Your new total is <strong>${total} points</strong>.</p>
    `,
  }),

  specialOffer: (offer: string, expiryDate: string): NotificationTemplate => ({
    subject: 'Special Offer Just for You!',
    text: `Don't miss out on our special offer: ${offer}. Valid until ${expiryDate}.`,
    html: `
      <h2>Special Offer Just for You!</h2>
      <p>Don't miss out on our special offer:</p>
      <h3>${offer}</h3>
      <p>Valid until ${expiryDate}</p>
    `,
  }),
}

class NotificationService {
  private static instance: NotificationService
  private rateLimitCache: { [key: string]: number } = {}
  private clients: Map<string, Set<WebSocket>> = new Map();
  private analyticsAlerts: Map<string, AnalyticsAlert[]> = new Map();

  private constructor() {
    this.startAnalyticsMonitoring();
  }

  static getInstance(): NotificationService {
    if (!NotificationService.instance) {
      NotificationService.instance = new NotificationService()
    }
    return NotificationService.instance
  }

  private async startAnalyticsMonitoring() {
    setInterval(async () => {
      try {
        const restaurants = await Restaurant.find({ active: true });
        for (const restaurant of restaurants) {
          await this.checkAnalyticsAlerts(restaurant._id.toString());
        }
      } catch (error) {
        logger.error('Error monitoring analytics:', error);
      }
    }, 5 * 60 * 1000); // Check every 5 minutes
  }

  private async checkAnalyticsAlerts(restaurantId: string) {
    const alerts = this.analyticsAlerts.get(restaurantId) || [];
    if (alerts.length === 0) return;

    try {
      const analytics = await analyticsService.getRestaurantAnalytics(restaurantId);
      const dailyMetrics = analytics.dailyMetrics;

      for (const alert of alerts) {
        let currentValue: number;

        // Get the current value based on the metric
        switch (alert.metric) {
          case 'totalSales':
            currentValue = dailyMetrics.sales.totalSales;
            break;
          case 'averageOrderValue':
            currentValue = dailyMetrics.sales.averageOrderValue;
            break;
          case 'orderCount':
            currentValue = dailyMetrics.sales.orderCount;
            break;
          case 'customerSatisfaction':
            currentValue = dailyMetrics.performance.customerSatisfactionScore;
            break;
          case 'orderCompletionRate':
            currentValue = dailyMetrics.performance.orderCompletionRate;
            break;
          default:
            continue;
        }

        // Check if alert condition is met
        if (
          (alert.condition === 'above' && currentValue > alert.threshold) ||
          (alert.condition === 'below' && currentValue < alert.threshold)
        ) {
          const notification: NotificationPayload = {
            type: 'analytics_alert',
            title: 'Analytics Alert',
            message: alert.message.replace('{value}', currentValue.toString()),
            severity: 'warning',
            data: {
              metric: alert.metric,
              value: currentValue,
              threshold: alert.threshold
            },
            timestamp: new Date()
          };

          await this.sendNotificationToRestaurant(restaurantId, notification);
        }
      }
    } catch (error) {
      logger.error(`Error checking analytics alerts for restaurant ${restaurantId}:`, error);
    }
  }

  public async setAnalyticsAlerts(restaurantId: string, alerts: AnalyticsAlert[]) {
    this.analyticsAlerts.set(restaurantId, alerts);
    await cacheSet(`analytics_alerts:${restaurantId}`, JSON.stringify(alerts));
  }

  public async getAnalyticsAlerts(restaurantId: string): Promise<AnalyticsAlert[]> {
    const cached = await cacheGet(`analytics_alerts:${restaurantId}`);
    if (cached) {
      return JSON.parse(cached);
    }
    return this.analyticsAlerts.get(restaurantId) || [];
  }

  public async addClient(userId: string, restaurantId: string, ws: WebSocket) {
    const key = `${userId}:${restaurantId}`;
    if (!this.clients.has(key)) {
      this.clients.set(key, new Set());
    }
    this.clients.get(key)?.add(ws);

    ws.on('close', () => {
      this.removeClient(userId, restaurantId, ws);
    });

    // Send any pending notifications
    const pendingNotifications = await this.getPendingNotifications(userId, restaurantId);
    for (const notification of pendingNotifications) {
      ws.send(JSON.stringify(notification));
    }
  }

  public removeClient(userId: string, restaurantId: string, ws: WebSocket) {
    const key = `${userId}:${restaurantId}`;
    this.clients.get(key)?.delete(ws);
    if (this.clients.get(key)?.size === 0) {
      this.clients.delete(key);
    }
  }

  public async sendNotificationToRestaurant(
    restaurantId: string,
    notification: NotificationPayload
  ) {
    try {
      // Store notification in cache for persistence
      const cacheKey = `notifications:${restaurantId}`;
      const existingNotifications = JSON.parse(
        (await cacheGet(cacheKey)) || '[]'
      );
      existingNotifications.push(notification);
      await cacheSet(cacheKey, JSON.stringify(existingNotifications));

      // Get all users associated with this restaurant
      const users = await User.find({ 
        restaurants: restaurantId,
        role: { $in: ['owner', 'manager'] }
      });

      // Send to all connected clients for this restaurant
      for (const user of users) {
        const key = `${user._id}:${restaurantId}`;
        const clients = this.clients.get(key);
        if (clients) {
          for (const client of clients) {
            if (client.readyState === WebSocket.OPEN) {
              client.send(JSON.stringify(notification));
            }
          }
        }
      }
    } catch (error) {
      logger.error('Error sending notification:', error);
    }
  }

  private async getPendingNotifications(
    userId: string,
    restaurantId: string
  ): Promise<NotificationPayload[]> {
    const cacheKey = `notifications:${restaurantId}`;
    const notifications = JSON.parse((await cacheGet(cacheKey)) || '[]');
    return notifications;
  }

  public async clearNotifications(restaurantId: string) {
    await cacheSet(`notifications:${restaurantId}`, '[]');
  }

  async sendEmail(options: EmailOptions): Promise<boolean> {
    try {
      await transporter.sendMail({
        from: process.env.SMTP_FROM,
        ...options,
      })
      return true
    } catch (error) {
      logger.error('Email sending failed:', error)
      return false
    }
  }

  async canSendNotification(userId: string, type: string): Promise<boolean> {
    const key = `notification:${userId}:${type}`
    const lastSent = await cacheGet(key)
    
    if (!lastSent) {
      await cacheSet(key, Date.now(), 24 * 60 * 60) // 24 hours
      return true
    }

    const timeSinceLastNotification = Date.now() - parseInt(lastSent)
    const minimumInterval = this.getMinimumInterval(type)
    
    return timeSinceLastNotification >= minimumInterval
  }

  private getMinimumInterval(type: string): number {
    const intervals: { [key: string]: number } = {
      pointsExpiring: 7 * 24 * 60 * 60 * 1000, // 7 days
      pointsEarned: 24 * 60 * 60 * 1000,      // 1 day
      specialOffer: 3 * 24 * 60 * 60 * 1000,  // 3 days
    }
    return intervals[type] || 24 * 60 * 60 * 1000
  }

  async notifyPointsExpiring(email: string, userId: string, points: number, daysLeft: number): Promise<boolean> {
    if (!(await this.canSendNotification(userId, 'pointsExpiring'))) {
      return false
    }

    const template = templates.pointsExpiring(points, daysLeft)
    return this.sendEmail({
      to: email,
      ...template,
    })
  }

  async notifyPointsEarned(email: string, userId: string, points: number, total: number): Promise<boolean> {
    if (!(await this.canSendNotification(userId, 'pointsEarned'))) {
      return false
    }

    const template = templates.pointsEarned(points, total)
    return this.sendEmail({
      to: email,
      ...template,
    })
  }

  async sendSpecialOffer(email: string, userId: string, offer: string, expiryDate: string): Promise<boolean> {
    if (!(await this.canSendNotification(userId, 'specialOffer'))) {
      return false
    }

    const template = templates.specialOffer(offer, expiryDate)
    return this.sendEmail({
      to: email,
      ...template,
    })
  }

  async sendContactNotification(data: ContactNotificationData): Promise<void> {
    try {
      // Send notification to admin/superadmin
      const adminTemplate = data.type === 'RESTAURANT'
        ? `
          <h2>New Contact Form Submission for ${data.restaurantName}</h2>
          <p><strong>Ticket ID:</strong> ${data.ticketId}</p>
          <p><strong>Name:</strong> ${data.name}</p>
          <p><strong>Email:</strong> ${data.email}</p>
          <p><strong>Subject:</strong> ${data.subject}</p>
          <p><strong>Message:</strong></p>
          <p>${data.message}</p>
          <p>Please respond to this inquiry through your restaurant's admin dashboard.</p>
        `
        : `
          <h2>New Quisin Platform Inquiry</h2>
          <p><strong>Ticket ID:</strong> ${data.ticketId}</p>
          <p><strong>Name:</strong> ${data.name}</p>
          <p><strong>Email:</strong> ${data.email}</p>
          <p><strong>Subject:</strong> ${data.subject}</p>
          <p><strong>Message:</strong></p>
          <p>${data.message}</p>
          <p>Please respond to this inquiry through the superadmin dashboard.</p>
        `;

      await this.sendEmail({
        to: data.adminEmail,
        subject: `New Contact Form Submission: ${data.subject}`,
        html: adminTemplate
      });

      // Send confirmation to user
      const userTemplate = data.type === 'RESTAURANT'
        ? `
          <h2>Thank you for contacting ${data.restaurantName}</h2>
          <p>Dear ${data.name},</p>
          <p>We have received your message and the restaurant's management will get back to you as soon as possible.</p>
          <p>Your ticket ID is: ${data.ticketId}</p>
          <p>For reference, here is a copy of your message:</p>
          <p><strong>Subject:</strong> ${data.subject}</p>
          <p><strong>Message:</strong></p>
          <p>${data.message}</p>
          <p>Best regards,<br>${data.restaurantName} Team</p>
        `
        : `
          <h2>Thank you for contacting Quisin</h2>
          <p>Dear ${data.name},</p>
          <p>We have received your message and will get back to you as soon as possible.</p>
          <p>Your ticket ID is: ${data.ticketId}</p>
          <p>For reference, here is a copy of your message:</p>
          <p><strong>Subject:</strong> ${data.subject}</p>
          <p><strong>Message:</strong></p>
          <p>${data.message}</p>
          <p>Best regards,<br>The Quisin Team</p>
        `;

      await this.sendEmail({
        to: data.email,
        subject: data.type === 'RESTAURANT' 
          ? `Thank you for contacting ${data.restaurantName}`
          : 'Thank you for contacting Quisin',
        html: userTemplate
      });

      // Send push notification with sound
      await this.sendPushNotification({
        userId: data.adminEmail,
        title: 'New Contact Form Submission',
        body: `New ${data.type.toLowerCase()} inquiry from ${data.name}`,
        sound: 'notification.mp3',
        data: {
          ticketId: data.ticketId,
          type: 'contact_form'
        }
      });
    } catch (error) {
      console.error('Error sending contact notification:', error);
      throw error;
    }
  }

  async sendTicketStatusNotification(data: TicketStatusNotificationData): Promise<void> {
    try {
      // Notify customer of status update
      await this.sendEmail({
        to: data.customerEmail,
        subject: `Ticket #${data.ticketId} Status Update`,
        html: `
          <h2>Your Ticket Status Has Been Updated</h2>
          <p>Your ticket (ID: ${data.ticketId}) has been updated to: ${data.status}</p>
          <p>We will continue to keep you updated on any changes.</p>
          <p>Best regards,<br>The Support Team</p>
        `
      });

      // If ticket is assigned to someone, notify them
      if (data.assignedTo) {
        await this.sendEmail({
          to: data.assignedTo,
          subject: `Ticket #${data.ticketId} Assigned to You`,
          html: `
            <h2>Ticket Assignment Notification</h2>
            <p>Ticket #${data.ticketId} has been assigned to you.</p>
            <p>Current status: ${data.status}</p>
            <p>Please review and take appropriate action.</p>
          `
        });

        // Send push notification with sound
        await this.sendPushNotification({
          userId: data.assignedTo,
          title: 'Ticket Assignment',
          body: `Ticket #${data.ticketId} has been assigned to you`,
          sound: 'assignment.mp3',
          data: {
            ticketId: data.ticketId,
            type: 'ticket_assignment'
          }
        });
      }
    } catch (error) {
      console.error('Error sending ticket status notification:', error);
      throw error;
    }
  }

  async sendPushNotification({ userId, title, body, sound, data }) {
    // Implementation will depend on your push notification service
    // Example using Firebase Cloud Messaging:
    try {
      await admin.messaging().send({
        token: await this.getUserPushToken(userId),
        notification: {
          title,
          body,
          sound
        },
        data
      });
    } catch (error) {
      console.error('Error sending push notification:', error);
      throw error;
    }
  }

  private async getUserPushToken(userId: string): Promise<string> {
    // Implementation to get user's push notification token
    // This would typically be stored in your database
    return 'user_push_token';
  }
}

export const notificationService = NotificationService.getInstance();
