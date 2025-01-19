import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CashPaymentForm from '../payment/CashPaymentForm.vue'
import { usePayment } from '@/composables/usePayment'
import { PaymentStatus } from '@/types/payment'
import type { PaymentResponse } from '@/types/payment'

vi.mock('@/composables/usePayment', () => ({
  usePayment: vi.fn(() => ({
    createCashPayment: vi.fn(),
    getPendingCashPayments: vi.fn()
  }))
}))

describe('CashPaymentForm', () => {
  const defaultProps = {
    orderId: 'test-order-id',
    amount: 100
  }

  beforeEach(() => {
    vi.clearAllMocks()
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('renders correctly', () => {
    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    expect(wrapper.find('[data-test="amount-display"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Amount to Pay:')
    expect(wrapper.text()).toContain('$100.00')
    expect(wrapper.find('[data-test="create-payment-button"]').text()).toBe('Create Cash Payment')
  })

  it('displays loading state when processing', async () => {
    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    await wrapper.setData({ isProcessing: true })
    expect(wrapper.find('[data-test="loading-spinner"]').exists()).toBe(true)
    expect(wrapper.find('[data-test="create-payment-button"]').text()).toBe('Processing...')
  })

  it('handles successful payment creation', async () => {
    const mockPaymentCode = 'TEST123'
    const mockPaymentId = 'test-payment-id'
    const mockCreatePayment = vi.fn().mockResolvedValue({
      id: mockPaymentId,
      paymentCode: mockPaymentCode,
      status: PaymentStatus.PENDING
    } as PaymentResponse)

    vi.mocked(usePayment).mockReturnValue({
      createCashPayment: mockCreatePayment,
      getPendingCashPayments: vi.fn()
    })

    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    await wrapper.find('[data-test="create-payment-button"]').trigger('click')

    expect(mockCreatePayment).toHaveBeenCalledWith({
      orderId: defaultProps.orderId,
      amount: defaultProps.amount
    })

    expect(wrapper.find('[data-test="payment-code"]').text()).toContain(mockPaymentCode)
    expect(wrapper.find('[data-test="status-message"]').text()).toBe('Please proceed to the cashier for payment')
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.PENDING)
  })

  it('handles payment creation error', async () => {
    const mockError = new Error('Failed to create payment')
    const mockCreatePayment = vi.fn().mockRejectedValue(mockError)

    vi.mocked(usePayment).mockReturnValue({
      createCashPayment: mockCreatePayment,
      getPendingCashPayments: vi.fn()
    })

    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    await wrapper.find('[data-test="create-payment-button"]').trigger('click')

    expect(wrapper.emitted('payment-error')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.FAILED)
    expect(wrapper.find('[data-test="status-message"]').text()).toBe('Failed to create cash payment')
  })

  it('polls for payment status after creation', async () => {
    const mockPaymentId = 'test-payment-id'
    const mockCreatePayment = vi.fn().mockResolvedValue({
      id: mockPaymentId,
      paymentCode: 'TEST123',
      status: PaymentStatus.PENDING
    } as PaymentResponse)

    const mockGetPendingPayments = vi.fn().mockResolvedValue([{
      id: mockPaymentId,
      status: PaymentStatus.SUCCESSFUL
    }] as PaymentResponse[])

    vi.mocked(usePayment).mockReturnValue({
      createCashPayment: mockCreatePayment,
      getPendingCashPayments: mockGetPendingPayments
    })

    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    await wrapper.find('[data-test="create-payment-button"]').trigger('click')
    await vi.runOnlyPendingTimers()

    expect(mockGetPendingPayments).toHaveBeenCalledWith(mockPaymentId)
    expect(wrapper.emitted('payment-success')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.SUCCESSFUL)
    expect(wrapper.find('[data-test="status-message"]').text()).toBe('Payment successful!')
  })

  it('handles payment status polling timeout', async () => {
    const mockPaymentId = 'test-payment-id'
    const mockCreatePayment = vi.fn().mockResolvedValue({
      id: mockPaymentId,
      paymentCode: 'TEST123',
      status: PaymentStatus.PENDING
    } as PaymentResponse)

    const mockGetPendingPayments = vi.fn().mockResolvedValue([])

    vi.mocked(usePayment).mockReturnValue({
      createCashPayment: mockCreatePayment,
      getPendingCashPayments: mockGetPendingPayments
    })

    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    await wrapper.find('[data-test="create-payment-button"]').trigger('click')
    await vi.advanceTimersByTime(5 * 60 * 1000) // 5 minutes

    expect(wrapper.emitted('payment-error')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.FAILED)
    expect(wrapper.find('[data-test="status-message"]').text()).toBe('Payment timeout. Please try again.')
  })

  it('handles failed payment status', async () => {
    const mockPaymentId = 'test-payment-id'
    const mockCreatePayment = vi.fn().mockResolvedValue({
      id: mockPaymentId,
      paymentCode: 'TEST123',
      status: PaymentStatus.PENDING
    } as PaymentResponse)

    const mockGetPendingPayments = vi.fn().mockResolvedValue([{
      id: mockPaymentId,
      status: PaymentStatus.FAILED
    }] as PaymentResponse[])

    vi.mocked(usePayment).mockReturnValue({
      createCashPayment: mockCreatePayment,
      getPendingCashPayments: mockGetPendingPayments
    })

    const wrapper = mount(CashPaymentForm, {
      props: defaultProps
    })

    await wrapper.find('[data-test="create-payment-button"]').trigger('click')
    await vi.runOnlyPendingTimers()

    expect(wrapper.emitted('payment-error')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.FAILED)
    expect(wrapper.find('[data-test="status-message"]').text()).toBe('Payment failed')
  })
}) 