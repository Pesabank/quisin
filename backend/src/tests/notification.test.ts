import { NotificationService } from '../services/notification'
import { connectTestDB, closeTestDB, clearTestDB } from './helpers/db'

jest.mock('nodemailer', () => ({
  createTransport: jest.fn().mockReturnValue({
    sendMail: jest.fn().mockImplementation((mailOptions) => 
      Promise.resolve({ messageId: 'test-message-id' })
    ),
  }),
}))

describe('NotificationService', () => {
  let notificationService: NotificationService

  beforeAll(async () => {
    await connectTestDB()
    notificationService = NotificationService.getInstance()
  })

  afterAll(async () => {
    await clearTestDB()
    await closeTestDB()
  })

  beforeEach(async () => {
    await clearTestDB()
  })

  describe('Rate Limiting', () => {
    it('should allow first notification', async () => {
      const canSend = await notificationService.canSendNotification(
        'user123',
        'pointsEarned'
      )
      expect(canSend).toBe(true)
    })

    it('should block repeated notifications within time limit', async () => {
      await notificationService.canSendNotification('user123', 'pointsEarned')
      const canSendAgain = await notificationService.canSendNotification(
        'user123',
        'pointsEarned'
      )
      expect(canSendAgain).toBe(false)
    })
  })

  describe('Email Notifications', () => {
    it('should send points expiring notification', async () => {
      const result = await notificationService.notifyPointsExpiring(
        'test@example.com',
        'user123',
        100,
        7
      )
      expect(result).toBe(true)
    })

    it('should send points earned notification', async () => {
      const result = await notificationService.notifyPointsEarned(
        'test@example.com',
        'user123',
        50,
        150
      )
      expect(result).toBe(true)
    })

    it('should send special offer notification', async () => {
      const result = await notificationService.sendSpecialOffer(
        'test@example.com',
        'user123',
        '20% off your next order',
        '2024-12-31'
      )
      expect(result).toBe(true)
    })
  })

  describe('Email Templates', () => {
    it('should generate correct points expiring template', async () => {
      const template = notificationService['templates'].pointsExpiring(100, 7)
      expect(template.subject).toContain('Expiring')
      expect(template.text).toContain('100 points')
      expect(template.text).toContain('7 days')
    })

    it('should generate correct points earned template', async () => {
      const template = notificationService['templates'].pointsEarned(50, 150)
      expect(template.subject).toContain('Earned')
      expect(template.text).toContain('50 points')
      expect(template.text).toContain('150 points')
    })

    it('should generate correct special offer template', async () => {
      const template = notificationService['templates'].specialOffer(
        '20% off',
        '2024-12-31'
      )
      expect(template.subject).toContain('Special Offer')
      expect(template.text).toContain('20% off')
      expect(template.text).toContain('2024-12-31')
    })
  })
})
