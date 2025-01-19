import { defineStore } from 'pinia'
import axios, { AxiosError } from 'axios'
import type {
  PaymentState,
  PaymentMethodData,
  PaymentRequest,
  PaymentResponse,
  ErrorResponse,
  CashPaymentConfirmation,
  CashPaymentCancellation
} from '@/types/payment'

export const usePaymentStore = defineStore('payment', {
  state: (): PaymentState => ({
    isLoading: false,
    error: null
  }),

  actions: {
    async createPaymentMethod(data: PaymentMethodData) {
      try {
        this.isLoading = true
        const response = await axios.post<PaymentResponse>('/api/v1/payments/methods', data)
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to create payment method'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async processPayment(data: PaymentRequest) {
      try {
        this.isLoading = true
        const response = await axios.post<PaymentResponse>('/api/v1/payments', {
          orderId: data.orderId,
          amount: data.amount,
          paymentMethodId: data.paymentMethodId,
          paymentMethod: 'CREDIT_CARD',
          paymentType: 'SINGLE_ORDER',
          currency: 'USD'
        })
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Payment failed'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async initiateMpesaPayment(data: PaymentRequest) {
      try {
        this.isLoading = true
        const response = await axios.post<PaymentResponse>('/api/v1/payments/mpesa/initiate', {
          orderId: data.orderId,
          amount: data.amount,
          phoneNumber: data.phoneNumber,
          paymentMethod: 'MOBILE_MONEY',
          paymentType: 'SINGLE_ORDER',
          currency: 'KES'
        })
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to initiate M-PESA payment'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async checkMpesaPaymentStatus(checkoutRequestId: string) {
      try {
        const response = await axios.get<PaymentResponse>(`/api/v1/payments/mpesa/status/${checkoutRequestId}`)
        return response.data.status
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to check payment status'
        throw err
      }
    },

    async initiatePayPalPayment(data: PaymentRequest) {
      try {
        this.isLoading = true
        const response = await axios.post<PaymentResponse>('/api/v1/payments/paypal/initiate', {
          orderId: data.orderId,
          amount: data.amount,
          paymentMethod: 'DIGITAL_WALLET',
          paymentType: 'SINGLE_ORDER',
          currency: 'USD'
        })
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to initiate PayPal payment'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async createCashPayment(data: PaymentRequest) {
      try {
        this.isLoading = true
        const response = await axios.post<PaymentResponse>('/api/v1/payments', {
          orderId: data.orderId,
          amount: data.amount,
          paymentMethod: 'CASH',
          paymentType: 'SINGLE_ORDER',
          currency: 'USD'
        })
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to create cash payment'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async getPendingCashPayments(restaurantId: string) {
      try {
        const response = await axios.get<PaymentResponse[]>(`/api/v1/payments/cash/pending?restaurantId=${restaurantId}`)
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to fetch pending cash payments'
        throw err
      }
    },

    async getPaymentStatus(paymentId: string) {
      try {
        this.isLoading = true
        const response = await axios.get<PaymentResponse>(`/api/v1/payments/${paymentId}`)
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to fetch payment status'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async confirmCashPayment(paymentId: string, data: CashPaymentConfirmation) {
      try {
        this.isLoading = true
        const response = await axios.patch<PaymentResponse>(`/api/v1/payments/cash/${paymentId}/confirm`, null, {
          params: data
        })
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to confirm cash payment'
        throw err
      } finally {
        this.isLoading = false
      }
    },

    async cancelCashPayment(paymentId: string, data: CashPaymentCancellation) {
      try {
        this.isLoading = true
        const response = await axios.patch<PaymentResponse>(`/api/v1/payments/cash/${paymentId}/cancel`, null, {
          params: data
        })
        return response.data
      } catch (error) {
        const err = error as AxiosError<ErrorResponse>
        this.error = err.response?.data?.message || 'Failed to cancel cash payment'
        throw err
      } finally {
        this.isLoading = false
      }
    }
  }
}) 