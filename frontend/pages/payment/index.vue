<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
      <!-- Payment Header -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Payment</h1>
        <p class="mt-2 text-lg text-gray-600">Choose your preferred payment method</p>
      </div>

      <!-- Order Summary -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-8">
        <div class="px-4 py-5 sm:px-6">
          <h2 class="text-lg font-medium text-gray-900">Order Summary</h2>
        </div>
        <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
          <dl class="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
            <div>
              <dt class="text-sm font-medium text-gray-500">Order ID</dt>
              <dd class="mt-1 text-sm text-gray-900">{{ orderId }}</dd>
            </div>
            <div>
              <dt class="text-sm font-medium text-gray-500">Total Amount</dt>
              <dd class="mt-1 text-sm text-gray-900">{{ formatCurrency(amount) }}</dd>
            </div>
          </dl>
        </div>
      </div>

      <!-- Payment Methods -->
      <div class="bg-white shadow sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Payment Methods</h3>
          
          <!-- Payment Method Selection -->
          <div class="space-y-4">
            <!-- Credit/Debit Card -->
            <div class="relative flex items-center p-4 border rounded-lg" 
                 :class="{ 'border-orange-500 bg-orange-50': selectedMethod === 'CARD', 'border-gray-200': selectedMethod !== 'CARD' }"
                 @click="selectedMethod = 'CARD'">
              <div class="flex items-center h-5">
                <input type="radio" v-model="selectedMethod" value="CARD" class="h-4 w-4 text-orange-600 border-gray-300 focus:ring-orange-500">
              </div>
              <div class="ml-3">
                <label class="font-medium text-gray-900">Credit/Debit Card</label>
                <div class="mt-1 flex space-x-2">
                  <img src="/images/visa.svg" alt="Visa" class="h-8">
                  <img src="/images/mastercard.svg" alt="Mastercard" class="h-8">
                </div>
              </div>
            </div>

            <!-- M-PESA -->
            <div class="relative flex items-center p-4 border rounded-lg"
                 :class="{ 'border-orange-500 bg-orange-50': selectedMethod === 'MPESA', 'border-gray-200': selectedMethod !== 'MPESA' }"
                 @click="selectedMethod = 'MPESA'">
              <div class="flex items-center h-5">
                <input type="radio" v-model="selectedMethod" value="MPESA" class="h-4 w-4 text-orange-600 border-gray-300 focus:ring-orange-500">
              </div>
              <div class="ml-3">
                <label class="font-medium text-gray-900">M-PESA</label>
                <p class="text-sm text-gray-500">Pay with M-PESA mobile money</p>
              </div>
            </div>

            <!-- PayPal -->
            <div class="relative flex items-center p-4 border rounded-lg"
                 :class="{ 'border-orange-500 bg-orange-50': selectedMethod === 'PAYPAL', 'border-gray-200': selectedMethod !== 'PAYPAL' }"
                 @click="selectedMethod = 'PAYPAL'">
              <div class="flex items-center h-5">
                <input type="radio" v-model="selectedMethod" value="PAYPAL" class="h-4 w-4 text-orange-600 border-gray-300 focus:ring-orange-500">
              </div>
              <div class="ml-3">
                <label class="font-medium text-gray-900">PayPal</label>
                <div class="mt-1">
                  <img src="/images/paypal.svg" alt="PayPal" class="h-8">
                </div>
              </div>
            </div>

            <!-- Cash -->
            <div class="relative flex items-center p-4 border rounded-lg"
                 :class="{ 'border-orange-500 bg-orange-50': selectedMethod === 'CASH', 'border-gray-200': selectedMethod !== 'CASH' }"
                 @click="selectedMethod = 'CASH'">
              <div class="flex items-center h-5">
                <input type="radio" v-model="selectedMethod" value="CASH" class="h-4 w-4 text-orange-600 border-gray-300 focus:ring-orange-500">
              </div>
              <div class="ml-3">
                <label class="font-medium text-gray-900">Cash Payment</label>
                <p class="text-sm text-gray-500">Pay with cash at the restaurant</p>
              </div>
            </div>
          </div>

          <!-- Payment Form -->
          <div class="mt-8">
            <component 
              :is="paymentFormComponent" 
              v-if="selectedMethod"
              :amount="amount"
              :order-id="orderId"
              @payment-success="handlePaymentSuccess"
              @payment-error="handlePaymentError"
            />
          </div>
        </div>
      </div>

      <!-- Success/Error Messages -->
      <div v-if="message" :class="['mt-4 p-4 rounded-md', messageClass]">
        <p class="text-sm">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import CardPaymentForm from '@/components/payment/CardPaymentForm.vue'
import MpesaPaymentForm from '@/components/payment/MpesaPaymentForm.vue'
import PayPalPaymentForm from '@/components/payment/PayPalPaymentForm.vue'
import CashPaymentForm from '@/components/payment/CashPaymentForm.vue'

type PaymentMethod = 'CARD' | 'MPESA' | 'PAYPAL' | 'CASH'

// Props
const props = defineProps<{
  orderId: string
  amount: number
}>()

// State
const selectedMethod = ref<PaymentMethod | ''>('')
const message = ref('')
const messageType = ref<'success' | 'error' | null>(null)

// Computed
const paymentFormComponent = computed(() => {
  switch (selectedMethod.value) {
    case 'CARD':
      return CardPaymentForm
    case 'MPESA':
      return MpesaPaymentForm
    case 'PAYPAL':
      return PayPalPaymentForm
    case 'CASH':
      return CashPaymentForm
    default:
      return null
  }
})

const messageClass = computed(() => {
  switch (messageType.value) {
    case 'success':
      return 'bg-green-50 text-green-800'
    case 'error':
      return 'bg-red-50 text-red-800'
    default:
      return ''
  }
})

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const handlePaymentSuccess = (): void => {
  message.value = 'Payment successful!'
  messageType.value = 'success'
  // Emit success event to parent
  emit('payment-success')
}

const handlePaymentError = (error: string): void => {
  message.value = error
  messageType.value = 'error'
  // Emit error event to parent
  emit('payment-error', error)
}

// Emits
const emit = defineEmits<{
  (e: 'payment-success'): void
  (e: 'payment-error', error: string): void
}>()
</script> 