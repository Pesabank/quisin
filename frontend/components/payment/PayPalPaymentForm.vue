<template>
  <div class="space-y-4">
    <div class="text-lg font-medium" data-test="amount-display">
      Amount to Pay: {{ formatCurrency(amount) }}
    </div>

    <div v-if="isProcessing" class="flex justify-center">
      <svg
        class="animate-spin h-8 w-8 text-primary-600"
        data-test="loading-spinner"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
      >
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
    </div>

    <div id="paypal-button-container" data-test="paypal-button-container"></div>

    <div v-if="paymentStatus" :class="[
      'rounded-md p-4',
      paymentStatus === 'PENDING' ? 'bg-yellow-50' : '',
      paymentStatus === 'SUCCESSFUL' ? 'bg-green-50' : '',
      paymentStatus === 'FAILED' ? 'bg-red-50' : ''
    ]">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg v-if="paymentStatus === 'PENDING'" class="h-5 w-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd" />
          </svg>
          <svg v-if="paymentStatus === 'SUCCESSFUL'" class="h-5 w-5 text-green-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          <svg v-if="paymentStatus === 'FAILED'" class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p :class="[
            'text-sm font-medium',
            paymentStatus === 'PENDING' ? 'text-yellow-800' : '',
            paymentStatus === 'SUCCESSFUL' ? 'text-green-800' : '',
            paymentStatus === 'FAILED' ? 'text-red-800' : ''
          ]" data-test="status-message">
            {{ statusMessage }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
declare global {
  interface Window {
    paypal: {
      Buttons: (config: {
        createOrder: () => Promise<string>
        onApprove: (data: { orderID: string }) => Promise<void>
        onCancel: () => void
        onError: (error: unknown) => void
      }) => {
        render: (containerId: string) => void
      }
    }
  }
}

import { ref, onMounted } from 'vue'
import { usePayment } from '@/composables/usePayment'
import { PaymentStatus } from '@/types/payment'

interface PaymentResponse {
  paypalOrderId: string
  status: PaymentStatus
  message: string
}

defineOptions({
  name: 'PayPalPaymentForm'
})

// Props
const props = defineProps<{
  orderId: string
  amount: number
}>()

// Emits
const emit = defineEmits<{
  (e: 'payment-success'): void
  (e: 'payment-error', error: string): void
}>()

// Composables
const { initiatePayPalPayment } = usePayment()

// State
const isProcessing = ref(false)
const paymentStatus = ref<PaymentStatus | null>(null)
const statusMessage = ref('')

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const handlePaymentSuccess = (data: { orderID: string }): void => {
  paymentStatus.value = PaymentStatus.SUCCESSFUL
  statusMessage.value = 'Payment successful!'
  emit('payment-success')
}

const handlePaymentError = (error: unknown): void => {
  paymentStatus.value = PaymentStatus.FAILED
  statusMessage.value = 'Payment failed'
  const errorMessage = error instanceof Error ? error.message : 'Payment failed'
  emit('payment-error', errorMessage)
}

const handlePaymentCancel = (): void => {
  paymentStatus.value = PaymentStatus.CANCELLED
  statusMessage.value = 'Payment cancelled'
  emit('payment-error', 'Payment cancelled by user')
}

// Initialize PayPal
onMounted(() => {
  if (window.paypal) {
    window.paypal.Buttons({
      createOrder: async () => {
        try {
          isProcessing.value = true
          paymentStatus.value = PaymentStatus.PENDING
          statusMessage.value = 'Processing payment...'

          const response = await initiatePayPalPayment({
            orderId: props.orderId,
            amount: props.amount
          })

          return response.paypalOrderId
        } catch (error: unknown) {
          handlePaymentError(error)
          throw error
        } finally {
          isProcessing.value = false
        }
      },
      onApprove: async (data: { orderID: string }) => {
        try {
          handlePaymentSuccess(data)
        } catch (error: unknown) {
          handlePaymentError(error)
        }
      },
      onCancel: handlePaymentCancel,
      onError: handlePaymentError
    }).render('#paypal-button-container')
  }
})
</script>

<style scoped>
#paypal-button-container {
  min-height: 150px;
}
</style> 