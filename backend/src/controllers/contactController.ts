import { Request, Response } from 'express';
import { NotificationService } from '../services/notification';
import { prisma } from '../services/db';
import { validateEmail } from '../utils/validation';

export class ContactController {
  private notificationService: NotificationService;

  constructor() {
    this.notificationService = new NotificationService();
  }

  async submitContactForm = async (req: Request, res: Response) => {
    try {
      const { name, email, subject, message, type, restaurantId } = req.body;

      // Validate required fields
      if (!name || !email || !subject || !message || !type) {
        return res.status(400).json({
          success: false,
          message: 'All fields are required'
        });
      }

      // Validate email format
      if (!validateEmail(email)) {
        return res.status(400).json({
          success: false,
          message: 'Invalid email format'
        });
      }

      // Validate type
      if (!['QUISIN', 'RESTAURANT'].includes(type)) {
        return res.status(400).json({
          success: false,
          message: 'Invalid ticket type'
        });
      }

      // If restaurant type, validate restaurant exists
      if (type === 'RESTAURANT' && restaurantId) {
        const restaurant = await prisma.restaurant.findUnique({
          where: { id: restaurantId },
          include: { admin: true }
        });

        if (!restaurant) {
          return res.status(400).json({
            success: false,
            message: 'Restaurant not found'
          });
        }

        // Create ticket with restaurant association
        const ticket = await prisma.contactTicket.create({
          data: {
            name,
            email,
            subject,
            message,
            type,
            restaurantId,
            assignedToId: restaurant.admin.id
          }
        });

        // Send notification to restaurant admin
        await this.notificationService.sendContactNotification({
          ticketId: ticket.id,
          name,
          email,
          subject,
          message,
          type,
          adminEmail: restaurant.admin.email,
          restaurantName: restaurant.name
        });

        return res.status(201).json({
          success: true,
          data: ticket
        });
      }

      // Create Quisin ticket
      const superadmin = await prisma.user.findFirst({
        where: { role: 'SUPERADMIN' }
      });

      const ticket = await prisma.contactTicket.create({
        data: {
          name,
          email,
          subject,
          message,
          type,
          assignedToId: superadmin?.id
        }
      });

      // Send notification to superadmin
      await this.notificationService.sendContactNotification({
        ticketId: ticket.id,
        name,
        email,
        subject,
        message,
        type,
        adminEmail: process.env.SUPERADMIN_EMAIL!
      });

      return res.status(201).json({
        success: true,
        data: ticket
      });
    } catch (error) {
      console.error('Error submitting contact form:', error);
      return res.status(500).json({
        success: false,
        message: 'Internal server error'
      });
    }
  };

  async getContactTickets = async (req: Request, res: Response) => {
    try {
      const { role, id } = req.user;
      let tickets;

      if (role === 'SUPERADMIN') {
        // Superadmin can see all tickets
        tickets = await prisma.contactTicket.findMany({
          include: {
            restaurant: true,
            assignedTo: true
          },
          orderBy: {
            createdAt: 'desc'
          }
        });
      } else if (role === 'ADMIN') {
        // Admin can only see their restaurant's tickets
        tickets = await prisma.contactTicket.findMany({
          where: {
            OR: [
              { assignedToId: id },
              { type: 'RESTAURANT', restaurant: { adminId: id } }
            ]
          },
          include: {
            restaurant: true,
            assignedTo: true
          },
          orderBy: {
            createdAt: 'desc'
          }
        });
      } else {
        return res.status(403).json({
          success: false,
          message: 'Unauthorized'
        });
      }

      return res.status(200).json({
        success: true,
        data: tickets
      });
    } catch (error) {
      console.error('Error fetching contact tickets:', error);
      return res.status(500).json({
        success: false,
        message: 'Internal server error'
      });
    }
  };

  async updateTicketStatus = async (req: Request, res: Response) => {
    try {
      const { id } = req.params;
      const { status, assignedToId } = req.body;
      const { role, id: userId } = req.user;

      const ticket = await prisma.contactTicket.findUnique({
        where: { id: parseInt(id) },
        include: { restaurant: true }
      });

      if (!ticket) {
        return res.status(404).json({
          success: false,
          message: 'Ticket not found'
        });
      }

      // Check authorization
      if (role !== 'SUPERADMIN' && 
          ticket.assignedToId !== userId && 
          ticket.restaurant?.adminId !== userId) {
        return res.status(403).json({
          success: false,
          message: 'Unauthorized'
        });
      }

      const validStatuses = ['NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
      if (!validStatuses.includes(status)) {
        return res.status(400).json({
          success: false,
          message: 'Invalid status'
        });
      }

      const updatedTicket = await prisma.contactTicket.update({
        where: { id: parseInt(id) },
        data: { 
          status,
          assignedToId: assignedToId || undefined
        },
        include: {
          restaurant: true,
          assignedTo: true
        }
      });

      // Send notification about status update
      await this.notificationService.sendTicketStatusNotification({
        ticketId: updatedTicket.id,
        status,
        assignedTo: updatedTicket.assignedTo?.email || '',
        customerEmail: updatedTicket.email
      });

      return res.status(200).json({
        success: true,
        data: updatedTicket
      });
    } catch (error) {
      console.error('Error updating ticket status:', error);
      return res.status(500).json({
        success: false,
        message: 'Internal server error'
      });
    }
  };
}
