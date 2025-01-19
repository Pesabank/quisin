<template>
  <div class="space-y-6">
    <!-- Instructions -->
    <div class="rounded-md bg-blue-50 p-4">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-blue-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-blue-800">How it works</h3>
          <div class="mt-2 text-sm text-blue-700">
            <ol class="list-decimal list-inside space-y-1">
              <li>Click on "Create Cash Payment" button</li>
              <li>Show the generated payment code to the cashier</li>
              <li>Pay the exact amount in cash</li>
              <li>Wait for payment confirmation</li>
            </ol>
          </div>
        </div>
      </div>
    </div>

    <!-- Amount Display -->
    <div class="rounded-md bg-gray-50 p-4">
      <div class="flex justify-between items-center">
        <span class="text-sm font-medium text-gray-700">Amount to Pay:</span>
        <span class="text-lg font-semibold text-gray-900">{{ formatCurrency(amount) }}</span>
      </div>
    </div>

    <!-- Payment Code Display -->
    <div v-if="paymentCode" class="rounded-md bg-white border-2 border-dashed border-gray-300 p-4">
      <div class="text-center">
        <h3 class="text-sm font-medium text-gray-900">Payment Code</h3>
        <div class="mt-2">
          <p class="text-2xl font-bold tracking-wider text-orange-600">{{ paymentCode }}</p>
        </div>
        <p class="mt-1 text-sm text-gray-500">Show this code to the cashier</p>
      </div>
    </div>

    <!-- Action Button -->
    <div>
      <button
        type="button"
        :disabled="isProcessing || paymentStatus === 'SUCCESSFUL'"
        @click="handleCashPayment"
        class="w-full flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 disabled:bg-gray-400 disabled:cursor-not-allowed"
      >
        <span v-if="isProcessing" class="mr-2">
          <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </span>
        {{ buttonText }}
      </button>
    </div>

    <!-- Status Messages -->
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
          ]">
            {{ statusMessage }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { usePayment } from '@/composables/usePayment'
import { PaymentStatus, type PaymentResponse } from '@/types/payment'

defineOptions({
  name: 'CashPaymentForm'
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
const { createCashPayment, getPendingCashPayments } = usePayment()

// State
const isProcessing = ref(false)
const paymentCode = ref<string | null>(null)
const paymentStatus = ref<PaymentStatus | null>(null)
const statusMessage = ref('')

// Computed
const buttonText = computed((): string => {
  if (isProcessing.value) return 'Processing...'
  if (paymentStatus.value === PaymentStatus.SUCCESSFUL) return 'Payment Completed'
  if (paymentCode.value) return 'Waiting for Payment'
  return 'Create Cash Payment'
})

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const handleCashPayment = async (): Promise<void> => {
  try {
    isProcessing.value = true
    paymentStatus.value = PaymentStatus.PENDING
    statusMessage.value = 'Creating cash payment...'

    const response = await createCashPayment({
      orderId: props.orderId,
      amount: props.amount
    })

    paymentCode.value = response.paymentCode
    statusMessage.value = 'Please proceed to the cashier for payment'

    // Start polling for payment status
    startPolling(response.id)
  } catch (error: unknown) {
    console.error('Error creating cash payment:', error)
    paymentStatus.value = PaymentStatus.FAILED
    const errorMessage = error instanceof Error ? error.message : 'Failed to create cash payment'
    statusMessage.value = errorMessage
    emit('payment-error', errorMessage)
  } finally {
    isProcessing.value = false
  }
}

const startPolling = async (paymentId: string): Promise<void> => {
  const pollInterval = 5000 // 5 seconds
  const maxAttempts = 60 // 5 minutes total

  let attempts = 0
  const poll = async (): Promise<void> => {
    try {
      const response = await getPendingCashPayments(paymentId)
      const payment = response.find((p: PaymentResponse) => p.id === paymentId)

      if (payment) {
        if (payment.status === PaymentStatus.SUCCESSFUL) {
          paymentStatus.value = PaymentStatus.SUCCESSFUL
          statusMessage.value = 'Payment successful!'
          emit('payment-success')
          return
        }

        if (payment.status === PaymentStatus.FAILED || payment.status === PaymentStatus.CANCELLED) {
          paymentStatus.value = payment.status
          statusMessage.value = `Payment ${payment.status.toLowerCase()}`
          emit('payment-error', `Payment ${payment.status.toLowerCase()}`)
          return
        }
      }

      attempts++
      if (attempts < maxAttempts) {
        setTimeout(poll, pollInterval)
      } else {
        paymentStatus.value = PaymentStatus.FAILED
        statusMessage.value = 'Payment timeout. Please try again.'
        emit('payment-error', 'Payment timeout')
      }
    } catch (error: unknown) {
      console.error('Error polling payment status:', error)
      paymentStatus.value = PaymentStatus.FAILED
      const errorMessage = error instanceof Error ? error.message : 'Error checking payment status'
      statusMessage.value = errorMessage
      emit('payment-error', errorMessage)
    }
  }

  await poll()
}
</script> 