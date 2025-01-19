<template>
  <div class="space-y-6">
    <!-- Phone Number Input -->
    <div>
      <label for="phoneNumber" class="block text-sm font-medium text-gray-700">M-PESA Phone Number</label>
      <div class="mt-1">
        <div class="flex">
          <span class="inline-flex items-center px-3 rounded-l-md border border-r-0 border-gray-300 bg-gray-50 text-gray-500 sm:text-sm">
            +254
          </span>
          <input
            type="tel"
            id="phoneNumber"
            v-model="phoneNumber"
            placeholder="712345678"
            maxlength="9"
            class="flex-1 block w-full rounded-none rounded-r-md border-gray-300 focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
          >
        </div>
        <p class="mt-1 text-sm text-gray-500">Enter the phone number registered with M-PESA</p>
      </div>
    </div>

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
              <li>Enter your M-PESA registered phone number</li>
              <li>Click on "Pay" button below</li>
              <li>You will receive a prompt on your phone</li>
              <li>Enter your M-PESA PIN to complete payment</li>
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

    <!-- Pay Button -->
    <div>
      <button
        type="button"
        :disabled="!isFormValid || isProcessing"
        @click="initiatePayment"
        class="w-full flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 disabled:bg-gray-400 disabled:cursor-not-allowed"
      >
        <span v-if="isProcessing" class="mr-2">
          <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </span>
        {{ isProcessing ? 'Processing...' : 'Pay with M-PESA' }}
      </button>
    </div>

    <!-- Status Messages -->
    <div v-if="paymentStatus" :class="[
      'rounded-md p-4',
      paymentStatus === 'pending' ? 'bg-yellow-50' : '',
      paymentStatus === 'success' ? 'bg-green-50' : '',
      paymentStatus === 'error' ? 'bg-red-50' : ''
    ]">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg v-if="paymentStatus === 'pending'" class="h-5 w-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd" />
          </svg>
          <svg v-if="paymentStatus === 'success'" class="h-5 w-5 text-green-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          <svg v-if="paymentStatus === 'error'" class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p :class="[
            'text-sm font-medium',
            paymentStatus === 'pending' ? 'text-yellow-800' : '',
            paymentStatus === 'success' ? 'text-green-800' : '',
            paymentStatus === 'error' ? 'text-red-800' : ''
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
import { usePaymentStore } from '@/stores/payment'

defineOptions({
  name: 'MpesaPaymentForm'
})

type PaymentStatus = 'pending' | 'success' | 'error' | null

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

// Store
const paymentStore = usePaymentStore()

// State
const isProcessing = ref(false)
const phoneNumber = ref('')
const paymentStatus = ref<PaymentStatus>(null)
const statusMessage = ref('')

// Computed
const isFormValid = computed((): boolean => {
  return phoneNumber.value.length === 9 && /^\d+$/.test(phoneNumber.value)
})

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-KE', {
    style: 'currency',
    currency: 'KES'
  }).format(amount)
}

const initiatePayment = async (): Promise<void> => {
  try {
    isProcessing.value = true
    paymentStatus.value = 'pending'
    statusMessage.value = 'Please check your phone for the M-PESA prompt...'

    // Initiate M-PESA STK Push
    const response = await paymentStore.initiateMpesaPayment({
      orderId: props.orderId,
      amount: props.amount,
      phoneNumber: `254${phoneNumber.value}`
    })

    // Start polling for payment status
    await pollPaymentStatus(response.checkoutRequestId)
  } catch (error: unknown) {
    paymentStatus.value = 'error'
    const errorMessage = error instanceof Error ? error.message : 'Payment failed'
    statusMessage.value = errorMessage
    emit('payment-error', errorMessage)
  } finally {
    isProcessing.value = false
  }
}

const pollPaymentStatus = async (checkoutRequestId: string): Promise<void> => {
  const maxAttempts = 30 // 30 seconds
  let attempts = 0

  const poll = async (): Promise<void> => {
    try {
      const status = await paymentStore.checkMpesaPaymentStatus(checkoutRequestId)
      
      if (status === 'COMPLETED') {
        paymentStatus.value = 'success'
        statusMessage.value = 'Payment successful!'
        emit('payment-success')
        return
      }
      
      if (status === 'FAILED') {
        paymentStatus.value = 'error'
        statusMessage.value = 'Payment failed. Please try again.'
        emit('payment-error', 'Payment failed')
        return
      }

      attempts++
      if (attempts < maxAttempts) {
        setTimeout(poll, 1000) // Poll every second
      } else {
        paymentStatus.value = 'error'
        statusMessage.value = 'Payment timeout. Please check your M-PESA messages.'
        emit('payment-error', 'Payment timeout')
      }
    } catch (error: unknown) {
      paymentStatus.value = 'error'
      const errorMessage = error instanceof Error ? error.message : 'Payment status check failed'
      statusMessage.value = errorMessage
      emit('payment-error', errorMessage)
    }
  }

  await poll()
}
</script> 