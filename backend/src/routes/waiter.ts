import express from 'express';
import { body, param } from 'express-validator';
import { validateRequest } from '../middleware/validateRequest';
import { TableStatus } from '../types/table';
import { prisma } from '../db';
import { Router } from 'express';
import { waiterController } from '../controllers/waiterController';
import { authenticate } from '../middleware/auth';
import { validate } from '../middleware/validation';

const router = express.Router();

// Get all tables
router.get('/tables', async (req, res) => {
  try {
    const tables = await prisma.table.findMany({
      include: {
        currentOrder: {
          include: {
            items: true
          }
        },
        reservation: true
      }
    });
    res.json(tables);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch tables' });
  }
});

// Update table status
router.patch(
  '/tables/:id/status',
  [
    param('id').isString(),
    body('status').isIn(Object.values(TableStatus)),
  ],
  validateRequest,
  async (req, res) => {
    const { id } = req.params;
    const { status } = req.body;

    try {
      const table = await prisma.table.update({
        where: { id },
        data: { status },
      });
      res.json(table);
    } catch (error) {
      res.status(500).json({ error: 'Failed to update table status' });
    }
  }
);

// Create new order
router.post(
  '/orders',
  [
    body('tableId').isString(),
    body('items').isArray(),
    body('items.*.menuItemId').isString(),
    body('items.*.quantity').isInt({ min: 1 }),
  ],
  validateRequest,
  async (req, res) => {
    const { tableId, items } = req.body;

    try {
      const order = await prisma.order.create({
        data: {
          tableId,
          status: 'PENDING',
          items: {
            create: items.map((item: any) => ({
              menuItemId: item.menuItemId,
              quantity: item.quantity,
            })),
          },
        },
        include: {
          items: {
            include: {
              menuItem: true,
            },
          },
        },
      });

      // Update table status
      await prisma.table.update({
        where: { id: tableId },
        data: { status: 'OCCUPIED' },
      });

      res.json(order);
    } catch (error) {
      res.status(500).json({ error: 'Failed to create order' });
    }
  }
);

// Get order details
router.get('/orders/:id', async (req, res) => {
  const { id } = req.params;

  try {
    const order = await prisma.order.findUnique({
      where: { id },
      include: {
        items: {
          include: {
            menuItem: true,
          },
        },
        table: true,
      },
    });

    if (!order) {
      return res.status(404).json({ error: 'Order not found' });
    }

    res.json(order);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch order' });
  }
});

// Generate bill for table
router.post(
  '/tables/:id/bill',
  [param('id').isString()],
  validateRequest,
  async (req, res) => {
    const { id } = req.params;

    try {
      const order = await prisma.order.findFirst({
        where: {
          tableId: id,
          status: 'COMPLETED',
        },
        include: {
          items: {
            include: {
              menuItem: true,
            },
          },
        },
      });

      if (!order) {
        return res.status(404).json({ error: 'No completed order found for table' });
      }

      const subtotal = order.items.reduce(
        (sum, item) => sum + item.quantity * item.menuItem.price,
        0
      );
      const tax = subtotal * 0.1; // 10% tax
      const total = subtotal + tax;

      const bill = await prisma.bill.create({
        data: {
          orderId: order.id,
          subtotal,
          tax,
          total,
          status: 'PENDING',
        },
      });

      res.json(bill);
    } catch (error) {
      res.status(500).json({ error: 'Failed to generate bill' });
    }
  }
);

// Process payment
router.post(
  '/bills/:id/pay',
  [
    param('id').isString(),
    body('amount').isFloat({ min: 0 }),
    body('method').isIn(['CASH', 'CARD', 'MOBILE']),
  ],
  validateRequest,
  async (req, res) => {
    const { id } = req.params;
    const { amount, method } = req.body;

    try {
      const bill = await prisma.bill.findUnique({
        where: { id },
      });

      if (!bill) {
        return res.status(404).json({ error: 'Bill not found' });
      }

      if (amount < bill.total) {
        return res.status(400).json({ error: 'Insufficient payment amount' });
      }

      const payment = await prisma.payment.create({
        data: {
          billId: id,
          amount,
          method,
          change: amount - bill.total,
        },
      });

      // Update bill status
      await prisma.bill.update({
        where: { id },
        data: { status: 'PAID' },
      });

      // Update table status
      const order = await prisma.order.findUnique({
        where: { id: bill.orderId },
      });

      if (order) {
        await prisma.table.update({
          where: { id: order.tableId },
          data: { status: 'AVAILABLE' },
        });
      }

      res.json(payment);
    } catch (error) {
      res.status(500).json({ error: 'Failed to process payment' });
    }
  }
);

const waiterRouter = Router();

waiterRouter.post(
  '/hail',
  authenticate,
  validate({
    body: {
      restaurantId: { type: 'string', required: true },
      tableId: { type: 'string', required: true }
    }
  }),
  waiterController.hailWaiter
);

waiterRouter.get(
  '/:restaurantId/:tableId/status',
  authenticate,
  validate({
    params: {
      restaurantId: { type: 'string', required: true },
      tableId: { type: 'string', required: true }
    }
  }),
  waiterController.getHailStatus
);

router.use('/waiter', waiterRouter);

export default router;
