import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PayPalPaymentForm from '../payment/PayPalPaymentForm.vue'
import { usePayment } from '@/composables/usePayment'
import { PaymentStatus } from '@/types/payment'

vi.mock('@/composables/usePayment', () => ({
  usePayment: vi.fn(() => ({
    initiatePayPalPayment: vi.fn()
  }))
}))

describe('PayPalPaymentForm', () => {
  const defaultProps = {
    orderId: 'test-order-id',
    amount: 100
  }

  beforeEach(() => {
    vi.clearAllMocks()
    // Mock PayPal SDK
    global.window.paypal = {
      Buttons: vi.fn((config) => ({
        render: vi.fn(),
        ...config
      }))
    }
  })

  it('renders correctly', () => {
    const wrapper = mount(PayPalPaymentForm, {
      props: defaultProps
    })

    expect(wrapper.find('#paypal-button-container').exists()).toBe(true)
    expect(wrapper.text()).toContain('Amount to Pay:')
    expect(wrapper.text()).toContain('$100.00')
  })

  it('displays loading state when processing', async () => {
    const wrapper = mount(PayPalPaymentForm, {
      props: defaultProps
    })

    await wrapper.setData({ isProcessing: true })
    expect(wrapper.find('[data-test="loading-spinner"]').exists()).toBe(true)
  })

  it('handles successful payment creation', async () => {
    const mockPaypalOrderId = 'test-paypal-order'
    const mockInitiatePayment = vi.fn().mockResolvedValue({ 
      paypalOrderId: mockPaypalOrderId 
    })

    vi.mocked(usePayment).mockReturnValue({
      initiatePayPalPayment: mockInitiatePayment
    })

    const wrapper = mount(PayPalPaymentForm, {
      props: defaultProps
    })

    const buttons = global.window.paypal.Buttons({
      createOrder: async () => {
        const response = await mockInitiatePayment({
          orderId: defaultProps.orderId,
          amount: defaultProps.amount
        })
        return response.paypalOrderId
      }
    })

    const orderId = await buttons.createOrder()
    expect(orderId).toBe(mockPaypalOrderId)
    expect(mockInitiatePayment).toHaveBeenCalledWith({
      orderId: defaultProps.orderId,
      amount: defaultProps.amount
    })
  })

  it('handles payment approval', async () => {
    const wrapper = mount(PayPalPaymentForm, {
      props: defaultProps
    })

    const buttons = global.window.paypal.Buttons({
      onApprove: async (data) => {
        wrapper.vm.handlePaymentSuccess(data)
      }
    })

    await buttons.onApprove({ orderID: 'test-order' })
    
    expect(wrapper.emitted('payment-success')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.SUCCESSFUL)
  })

  it('handles payment cancellation', async () => {
    const wrapper = mount(PayPalPaymentForm, {
      props: defaultProps
    })

    const buttons = global.window.paypal.Buttons({
      onCancel: () => {
        wrapper.vm.handlePaymentCancel()
      }
    })

    buttons.onCancel()
    
    expect(wrapper.emitted('payment-error')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.CANCELLED)
  })

  it('handles payment errors', async () => {
    const mockError = new Error('Payment failed')
    const mockInitiatePayment = vi.fn().mockRejectedValue(mockError)

    vi.mocked(usePayment).mockReturnValue({
      initiatePayPalPayment: mockInitiatePayment
    })

    const wrapper = mount(PayPalPaymentForm, {
      props: defaultProps
    })

    const buttons = global.window.paypal.Buttons({
      createOrder: async () => {
        try {
          const response = await mockInitiatePayment({
            orderId: defaultProps.orderId,
            amount: defaultProps.amount
          })
          return response.paypalOrderId
        } catch (error) {
          wrapper.vm.handlePaymentError(error as Error)
          throw error
        }
      }
    })

    try {
      await buttons.createOrder()
    } catch (error) {
      expect(error).toBe(mockError)
    }

    expect(wrapper.emitted('payment-error')).toBeTruthy()
    expect(wrapper.vm.paymentStatus).toBe(PaymentStatus.FAILED)
  })
}) 