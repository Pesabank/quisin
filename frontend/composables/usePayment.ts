import { usePaymentStore } from '@/stores/payment'
import type { PaymentMethod, PaymentRequest } from '@/types/payment'

export const usePayment = () => {
  const paymentStore = usePaymentStore()
  const { $axios } = useNuxtApp()

  const createPaymentMethod = async (data: PaymentMethod) => {
    try {
      return await paymentStore.createPaymentMethod(data)
    } catch (error) {
      console.error('Error creating payment method:', error)
      throw error
    }
  }

  const processPayment = async (data: PaymentRequest) => {
    try {
      return await paymentStore.processPayment(data)
    } catch (error) {
      console.error('Error processing payment:', error)
      throw error
    }
  }

  const initiateMpesaPayment = async (data: PaymentRequest) => {
    try {
      return await paymentStore.initiateMpesaPayment(data)
    } catch (error) {
      console.error('Error initiating M-PESA payment:', error)
      throw error
    }
  }

  const checkMpesaPaymentStatus = async (checkoutRequestId: string) => {
    try {
      return await paymentStore.checkMpesaPaymentStatus(checkoutRequestId)
    } catch (error) {
      console.error('Error checking M-PESA payment status:', error)
      throw error
    }
  }

  const initiatePayPalPayment = async (data: PaymentRequest) => {
    try {
      return await paymentStore.initiatePayPalPayment(data)
    } catch (error) {
      console.error('Error initiating PayPal payment:', error)
      throw error
    }
  }

  const createCashPayment = async (data: PaymentRequest) => {
    try {
      return await paymentStore.createCashPayment(data)
    } catch (error) {
      console.error('Error creating cash payment:', error)
      throw error
    }
  }

  const getPendingCashPayments = async (restaurantId: string) => {
    try {
      return await paymentStore.getPendingCashPayments(restaurantId)
    } catch (error) {
      console.error('Error fetching pending cash payments:', error)
      throw error
    }
  }

  const getPaymentStatus = async (paymentId: string) => {
    try {
      return await paymentStore.getPaymentStatus(paymentId)
    } catch (error) {
      console.error('Error fetching payment status:', error)
      throw error
    }
  }

  const confirmCashPayment = async (paymentId: string, data: { amountReceived: string, staffId: string }) => {
    try {
      return await paymentStore.confirmCashPayment(paymentId, data)
    } catch (error) {
      console.error('Error confirming cash payment:', error)
      throw error
    }
  }

  const cancelCashPayment = async (paymentId: string, data: { reason: string, staffId: string }) => {
    try {
      return await paymentStore.cancelCashPayment(paymentId, data)
    } catch (error) {
      console.error('Error canceling cash payment:', error)
      throw error
    }
  }

  return {
    createPaymentMethod,
    processPayment,
    initiateMpesaPayment,
    checkMpesaPaymentStatus,
    initiatePayPalPayment,
    createCashPayment,
    getPendingCashPayments,
    getPaymentStatus,
    confirmCashPayment,
    cancelCashPayment,
    isLoading: computed(() => paymentStore.isLoading),
    error: computed(() => paymentStore.error)
  }
} 