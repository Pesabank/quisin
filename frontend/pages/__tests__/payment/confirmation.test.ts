import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import PaymentConfirmation from '../../payment/confirmation.vue'
import { usePayment } from '@/composables/usePayment'
import { PaymentStatus, PaymentMethod } from '@/types/payment'

vi.mock('@/composables/usePayment', () => ({
  usePayment: vi.fn(() => ({
    getPaymentStatus: vi.fn()
  }))
}))

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/payment/confirmation',
      name: 'PaymentConfirmation',
      component: PaymentConfirmation
    },
    {
      path: '/orders/:id',
      name: 'OrderDetails',
      component: { template: '<div>Order Details</div>' }
    },
    {
      path: '/payment',
      name: 'Payment',
      component: { template: '<div>Payment</div>' }
    },
    {
      path: '/support',
      name: 'Support',
      component: { template: '<div>Support</div>' }
    }
  ]
})

describe('PaymentConfirmation', () => {
  const mockPayment = {
    id: 'test-payment-id',
    orderId: 'test-order-id',
    amount: 100,
    paymentMethod: PaymentMethod.CREDIT_CARD,
    status: PaymentStatus.SUCCESSFUL,
    externalTransactionId: 'test-transaction-id',
    createdAt: '2024-01-09T12:00:00Z'
  }

  beforeEach(() => {
    vi.clearAllMocks()
    router.push({
      path: '/payment/confirmation',
      query: { paymentId: 'test-payment-id' }
    })
  })

  it('displays loading state initially', () => {
    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    expect(wrapper.find('[data-test="loading-state"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Processing Payment')
  })

  it('displays success state for successful payment', async () => {
    const mockGetPaymentStatus = vi.fn().mockResolvedValue(mockPayment)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()

    expect(mockGetPaymentStatus).toHaveBeenCalledWith('test-payment-id')
    expect(wrapper.find('[data-test="success-state"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Payment Successful!')
    expect(wrapper.text()).toContain(mockPayment.orderId)
    expect(wrapper.text()).toContain('$100.00')
    expect(wrapper.text()).toContain('Credit Card')
  })

  it('displays error state for failed payment', async () => {
    const mockFailedPayment = { ...mockPayment, status: PaymentStatus.FAILED }
    const mockGetPaymentStatus = vi.fn().mockResolvedValue(mockFailedPayment)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()

    expect(wrapper.find('[data-test="error-state"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Payment Failed')
  })

  it('handles error when payment ID is missing', async () => {
    await router.push('/payment/confirmation')
    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()

    expect(wrapper.find('[data-test="error-state"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Payment ID is required')
  })

  it('handles API error', async () => {
    const mockError = new Error('Failed to fetch payment')
    const mockGetPaymentStatus = vi.fn().mockRejectedValue(mockError)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()

    expect(wrapper.find('[data-test="error-state"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Failed to fetch payment')
  })

  it('navigates to order details on view order click', async () => {
    const mockGetPaymentStatus = vi.fn().mockResolvedValue(mockPayment)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()
    await wrapper.find('button:first-child').trigger('click')

    expect(router.currentRoute.value.path).toBe(`/orders/${mockPayment.orderId}`)
  })

  it('initiates receipt download on download click', async () => {
    const mockGetPaymentStatus = vi.fn().mockResolvedValue(mockPayment)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const mockFetch = vi.fn().mockResolvedValue({
      blob: () => new Blob(['test'], { type: 'application/pdf' })
    })
    global.fetch = mockFetch

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()
    await wrapper.find('button:nth-child(2)').trigger('click')

    expect(mockFetch).toHaveBeenCalledWith(`/api/v1/payments/${mockPayment.externalTransactionId}/receipt`)
  })

  it('navigates to payment page on retry click', async () => {
    const mockFailedPayment = { ...mockPayment, status: PaymentStatus.FAILED }
    const mockGetPaymentStatus = vi.fn().mockResolvedValue(mockFailedPayment)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()
    await wrapper.find('button:first-child').trigger('click')

    expect(router.currentRoute.value.path).toBe('/payment')
    expect(router.currentRoute.value.query.orderId).toBe(mockFailedPayment.orderId)
  })

  it('navigates to support page on contact support click', async () => {
    const mockFailedPayment = { ...mockPayment, status: PaymentStatus.FAILED }
    const mockGetPaymentStatus = vi.fn().mockResolvedValue(mockFailedPayment)
    vi.mocked(usePayment).mockReturnValue({
      getPaymentStatus: mockGetPaymentStatus
    })

    const wrapper = mount(PaymentConfirmation, {
      global: {
        plugins: [router]
      }
    })

    await router.isReady()
    await wrapper.find('button:nth-child(2)').trigger('click')

    expect(router.currentRoute.value.path).toBe('/support')
  })
}) 