<template>
  <div class="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-3xl mx-auto">
      <!-- Loading State -->
      <div v-if="isLoading" class="text-center" data-test="loading-state">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-orange-100">
          <svg
            class="animate-spin h-8 w-8 text-orange-600"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>
        <h2 class="mt-4 text-lg font-medium text-gray-900">Processing Payment</h2>
        <p class="mt-2 text-sm text-gray-500">Please wait while we confirm your payment...</p>
      </div>

      <!-- Success State -->
      <div v-else-if="paymentStatus === PaymentStatus.SUCCESSFUL" class="text-center" data-test="success-state">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-green-100">
          <svg class="h-8 w-8 text-green-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h2 class="mt-4 text-lg font-medium text-gray-900">Payment Successful!</h2>
        <p class="mt-2 text-sm text-gray-500">Thank you for your payment. Your order has been confirmed.</p>
        
        <!-- Payment Details -->
        <div class="mt-8 bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg leading-6 font-medium text-gray-900">Payment Details</h3>
          </div>
          <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
            <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
              <div>
                <dt class="text-sm font-medium text-gray-500">Order ID</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ orderId }}</dd>
              </div>
              <div>
                <dt class="text-sm font-medium text-gray-500">Amount Paid</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ formatCurrency(amount) }}</dd>
              </div>
              <div>
                <dt class="text-sm font-medium text-gray-500">Payment Method</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ formatPaymentMethod(paymentMethod) }}</dd>
              </div>
              <div>
                <dt class="text-sm font-medium text-gray-500">Transaction ID</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ transactionId }}</dd>
              </div>
              <div>
                <dt class="text-sm font-medium text-gray-500">Date</dt>
                <dd class="mt-1 text-sm text-gray-900">{{ formatDate(paymentDate) }}</dd>
              </div>
            </dl>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="mt-8 space-y-4 sm:space-y-0 sm:flex sm:space-x-4 justify-center">
          <button
            type="button"
            @click="viewOrder"
            class="w-full sm:w-auto flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
          >
            View Order
          </button>
          <button
            type="button"
            @click="downloadReceipt"
            class="w-full sm:w-auto flex justify-center items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
          >
            Download Receipt
          </button>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="paymentStatus === PaymentStatus.FAILED" class="text-center" data-test="error-state">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-red-100">
          <svg class="h-8 w-8 text-red-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
        <h2 class="mt-4 text-lg font-medium text-gray-900">Payment Failed</h2>
        <p class="mt-2 text-sm text-gray-500">{{ errorMessage || 'There was an error processing your payment. Please try again.' }}</p>
        
        <!-- Action Buttons -->
        <div class="mt-8 space-y-4 sm:space-y-0 sm:flex sm:space-x-4 justify-center">
          <button
            type="button"
            @click="retryPayment"
            class="w-full sm:w-auto flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
          >
            Try Again
          </button>
          <button
            type="button"
            @click="contactSupport"
            class="w-full sm:w-auto flex justify-center items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
          >
            Contact Support
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePayment } from '@/composables/usePayment'
import { PaymentStatus, PaymentMethod } from '@/types/payment'

const route = useRoute()
const router = useRouter()
const { getPaymentStatus } = usePayment()

// State
const isLoading = ref(true)
const paymentStatus = ref<PaymentStatus | null>(null)
const errorMessage = ref<string | null>(null)
const orderId = ref<string>('')
const amount = ref<number>(0)
const paymentMethod = ref<PaymentMethod | null>(null)
const transactionId = ref<string>('')
const paymentDate = ref<Date | null>(null)

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const formatPaymentMethod = (method: PaymentMethod | null): string => {
  if (!method) return ''
  return method.split('_').map(word => word.charAt(0) + word.slice(1).toLowerCase()).join(' ')
}

const formatDate = (date: Date | null): string => {
  if (!date) return ''
  return new Intl.DateTimeFormat('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(date)
}

const viewOrder = () => {
  router.push(`/orders/${orderId.value}`)
}

const downloadReceipt = async () => {
  try {
    // Implement receipt download logic
    const response = await fetch(`/api/v1/payments/${transactionId.value}/receipt`)
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `receipt-${transactionId.value}.pdf`
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
  } catch (error) {
    console.error('Error downloading receipt:', error)
  }
}

const retryPayment = () => {
  router.push(`/payment?orderId=${orderId.value}`)
}

const contactSupport = () => {
  router.push('/support')
}

// Initialize
onMounted(async () => {
  try {
    const paymentId = route.query.paymentId as string
    if (!paymentId) {
      throw new Error('Payment ID is required')
    }

    const payment = await getPaymentStatus(paymentId)
    
    paymentStatus.value = payment.status
    orderId.value = payment.orderId
    amount.value = payment.amount
    paymentMethod.value = payment.paymentMethod
    transactionId.value = payment.externalTransactionId || payment.id
    paymentDate.value = new Date(payment.createdAt)
  } catch (error) {
    console.error('Error fetching payment status:', error)
    paymentStatus.value = PaymentStatus.FAILED
    errorMessage.value = error instanceof Error ? error.message : 'Failed to load payment details'
  } finally {
    isLoading.value = false
  }
})
</script> 