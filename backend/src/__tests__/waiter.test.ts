import request from 'supertest';
import { app } from '../app';
import { prisma } from '../db';
import jwt from 'jsonwebtoken';

describe('Waiter API Endpoints', () => {
  let authToken: string;
  let testTable: any;
  let testOrder: any;
  let testBill: any;

  beforeAll(async () => {
    // Create test user and generate token
    const user = await prisma.user.create({
      data: {
        email: 'test.waiter@quisin.com',
        password: 'hashedPassword',
        role: 'WAITER',
      },
    });

    authToken = jwt.sign(
      { userId: user.id, role: user.role },
      process.env.JWT_SECRET!
    );

    // Create test table
    testTable = await prisma.table.create({
      data: {
        number: 1,
        capacity: 4,
        status: 'AVAILABLE',
      },
    });
  });

  afterAll(async () => {
    // Clean up test data
    await prisma.payment.deleteMany();
    await prisma.bill.deleteMany();
    await prisma.orderItem.deleteMany();
    await prisma.order.deleteMany();
    await prisma.table.deleteMany();
    await prisma.user.deleteMany();
  });

  describe('GET /api/waiter/tables', () => {
    it('should return all tables', async () => {
      const response = await request(app)
        .get('/api/waiter/tables')
        .set('Authorization', `Bearer ${authToken}`);

      expect(response.status).toBe(200);
      expect(Array.isArray(response.body)).toBe(true);
      expect(response.body.length).toBeGreaterThan(0);
    });

    it('should require authentication', async () => {
      const response = await request(app).get('/api/waiter/tables');

      expect(response.status).toBe(401);
    });
  });

  describe('PATCH /api/waiter/tables/:id/status', () => {
    it('should update table status', async () => {
      const response = await request(app)
        .patch(`/api/waiter/tables/${testTable.id}/status`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ status: 'OCCUPIED' });

      expect(response.status).toBe(200);
      expect(response.body.status).toBe('OCCUPIED');
    });

    it('should validate status value', async () => {
      const response = await request(app)
        .patch(`/api/waiter/tables/${testTable.id}/status`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ status: 'INVALID_STATUS' });

      expect(response.status).toBe(400);
    });
  });

  describe('POST /api/waiter/orders', () => {
    it('should create a new order', async () => {
      const orderData = {
        tableId: testTable.id,
        items: [
          {
            menuItemId: 'menu-item-id',
            quantity: 2,
          },
        ],
      };

      const response = await request(app)
        .post('/api/waiter/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send(orderData);

      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('id');
      expect(response.body.status).toBe('PENDING');

      testOrder = response.body;
    });

    it('should validate order data', async () => {
      const response = await request(app)
        .post('/api/waiter/orders')
        .set('Authorization', `Bearer ${authToken}`)
        .send({});

      expect(response.status).toBe(400);
    });
  });

  describe('POST /api/waiter/tables/:id/bill', () => {
    it('should generate a bill for completed order', async () => {
      // First complete the order
      await prisma.order.update({
        where: { id: testOrder.id },
        data: { status: 'COMPLETED' },
      });

      const response = await request(app)
        .post(`/api/waiter/tables/${testTable.id}/bill`)
        .set('Authorization', `Bearer ${authToken}`);

      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('subtotal');
      expect(response.body).toHaveProperty('tax');
      expect(response.body).toHaveProperty('total');

      testBill = response.body;
    });
  });

  describe('POST /api/waiter/bills/:id/pay', () => {
    it('should process payment for bill', async () => {
      const paymentData = {
        amount: testBill.total,
        method: 'CASH',
      };

      const response = await request(app)
        .post(`/api/waiter/bills/${testBill.id}/pay`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(paymentData);

      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('change');
      expect(response.body.status).toBe('PAID');
    });

    it('should validate payment amount', async () => {
      const paymentData = {
        amount: 0,
        method: 'CASH',
      };

      const response = await request(app)
        .post(`/api/waiter/bills/${testBill.id}/pay`)
        .set('Authorization', `Bearer ${authToken}`)
        .send(paymentData);

      expect(response.status).toBe(400);
    });
  });
});
