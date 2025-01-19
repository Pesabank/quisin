import { Server } from 'socket.io';
import { Server as HttpServer } from 'http';
import jwt from 'jsonwebtoken';
import { prisma } from '../db';

export const initializeWebSocket = (httpServer: HttpServer) => {
  const io = new Server(httpServer, {
    cors: {
      origin: process.env.FRONTEND_URL,
      methods: ['GET', 'POST'],
      credentials: true,
    },
  });

  // Authentication middleware
  io.use(async (socket, next) => {
    try {
      const token = socket.handshake.auth.token;
      if (!token) {
        throw new Error('Authentication error');
      }

      const decoded = jwt.verify(token, process.env.JWT_SECRET!) as { userId: string };
      const user = await prisma.user.findUnique({
        where: { id: decoded.userId },
      });

      if (!user) {
        throw new Error('User not found');
      }

      socket.data.user = user;
      next();
    } catch (error) {
      next(new Error('Authentication error'));
    }
  });

  // Connection handler
  io.on('connection', (socket) => {
    console.log('Client connected:', socket.id);

    // Join room based on restaurant ID
    if (socket.data.user.restaurantId) {
      socket.join(`restaurant:${socket.data.user.restaurantId}`);
    }

    // Table status updates
    socket.on('table:status', async (data) => {
      try {
        const { tableId, status } = data;
        await prisma.table.update({
          where: { id: tableId },
          data: { status },
        });

        io.to(`restaurant:${socket.data.user.restaurantId}`).emit('table:updated', {
          tableId,
          status,
        });
      } catch (error) {
        console.error('Error updating table status:', error);
      }
    });

    // Order updates
    socket.on('order:status', async (data) => {
      try {
        const { orderId, status } = data;
        await prisma.order.update({
          where: { id: orderId },
          data: { status },
        });

        io.to(`restaurant:${socket.data.user.restaurantId}`).emit('order:updated', {
          orderId,
          status,
        });
      } catch (error) {
        console.error('Error updating order status:', error);
      }
    });

    // Staff schedule updates
    socket.on('schedule:update', async (data) => {
      try {
        const { scheduleId, updates } = data;
        await prisma.staffSchedule.update({
          where: { id: scheduleId },
          data: updates,
        });

        io.to(`restaurant:${socket.data.user.restaurantId}`).emit('schedule:updated', {
          scheduleId,
          ...updates,
        });
      } catch (error) {
        console.error('Error updating schedule:', error);
      }
    });

    socket.on('disconnect', () => {
      console.log('Client disconnected:', socket.id);
    });
  });

  return io;
};
