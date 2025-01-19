<template>
  <div class="space-y-6">
    <!-- Card Details -->
    <div class="space-y-4">
      <div>
        <label for="cardNumber" class="block text-sm font-medium text-gray-700">Card Number</label>
        <div class="mt-1">
          <input
            type="text"
            id="cardNumber"
            v-model="cardDetails.number"
            @input="formatCardNumber"
            maxlength="19"
            placeholder="4242 4242 4242 4242"
            class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
          >
        </div>
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div>
          <label for="expiry" class="block text-sm font-medium text-gray-700">Expiry Date</label>
          <div class="mt-1">
            <input
              type="text"
              id="expiry"
              v-model="cardDetails.expiry"
              @input="formatExpiry"
              maxlength="5"
              placeholder="MM/YY"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
            >
          </div>
        </div>

        <div>
          <label for="cvc" class="block text-sm font-medium text-gray-700">CVC</label>
          <div class="mt-1">
            <input
              type="text"
              id="cvc"
              v-model="cardDetails.cvc"
              maxlength="4"
              placeholder="123"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
            >
          </div>
        </div>
      </div>

      <div>
        <label for="name" class="block text-sm font-medium text-gray-700">Name on Card</label>
        <div class="mt-1">
          <input
            type="text"
            id="name"
            v-model="cardDetails.name"
            placeholder="John Doe"
            class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
          >
        </div>
      </div>
    </div>

    <!-- Billing Address -->
    <div class="space-y-4">
      <h4 class="text-sm font-medium text-gray-900">Billing Address</h4>
      
      <div>
        <label for="street" class="block text-sm font-medium text-gray-700">Street Address</label>
        <div class="mt-1">
          <input
            type="text"
            id="street"
            v-model="billingAddress.street"
            class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
          >
        </div>
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div>
          <label for="city" class="block text-sm font-medium text-gray-700">City</label>
          <div class="mt-1">
            <input
              type="text"
              id="city"
              v-model="billingAddress.city"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
            >
          </div>
        </div>

        <div>
          <label for="postalCode" class="block text-sm font-medium text-gray-700">Postal Code</label>
          <div class="mt-1">
            <input
              type="text"
              id="postalCode"
              v-model="billingAddress.postalCode"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 sm:text-sm"
            >
          </div>
        </div>
      </div>
    </div>

    <!-- Pay Button -->
    <div>
      <button
        type="button"
        :disabled="!isFormValid || isProcessing"
        @click="processPayment"
        class="w-full flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 disabled:bg-gray-400 disabled:cursor-not-allowed"
      >
        <span v-if="isProcessing" class="mr-2">
          <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </span>
        {{ isProcessing ? 'Processing...' : `Pay ${formatCurrency(amount)}` }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { usePaymentStore } from '@/stores/payment'

defineOptions({
  name: 'CardPaymentForm'
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

// Store
const paymentStore = usePaymentStore()

// State
const isProcessing = ref(false)
const cardDetails = ref({
  number: '',
  expiry: '',
  cvc: '',
  name: ''
})

const billingAddress = ref({
  street: '',
  city: '',
  postalCode: ''
})

// Computed
const isFormValid = computed(() => {
  return (
    cardDetails.value.number.length === 19 &&
    cardDetails.value.expiry.length === 5 &&
    cardDetails.value.cvc.length >= 3 &&
    cardDetails.value.name.length > 0 &&
    billingAddress.value.street.length > 0 &&
    billingAddress.value.city.length > 0 &&
    billingAddress.value.postalCode.length > 0
  )
})

// Methods
const formatCardNumber = (event: Event) => {
  const input = event.target as HTMLInputElement
  let value = input.value.replace(/\D/g, '')
  value = value.replace(/(\d{4})/g, '$1 ').trim()
  cardDetails.value.number = value
}

const formatExpiry = (event: Event) => {
  const input = event.target as HTMLInputElement
  let value = input.value.replace(/\D/g, '')
  if (value.length >= 2) {
    value = value.slice(0, 2) + '/' + value.slice(2)
  }
  cardDetails.value.expiry = value
}

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const processPayment = async () => {
  try {
    isProcessing.value = true
    
    // Create payment with Stripe
    const paymentMethod = await paymentStore.createPaymentMethod({
      type: 'card',
      card: {
        number: cardDetails.value.number.replace(/\s/g, ''),
        exp_month: parseInt(cardDetails.value.expiry.split('/')[0]),
        exp_year: parseInt('20' + cardDetails.value.expiry.split('/')[1]),
        cvc: cardDetails.value.cvc
      },
      billing_details: {
        name: cardDetails.value.name,
        address: {
          line1: billingAddress.value.street,
          city: billingAddress.value.city,
          postal_code: billingAddress.value.postalCode
        }
      }
    })

    // Process payment
    await paymentStore.processPayment({
      orderId: props.orderId,
      amount: props.amount,
      paymentMethodId: paymentMethod.id
    })

    emit('payment-success')
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : 'Payment processing failed'
    emit('payment-error', errorMessage)
  } finally {
    isProcessing.value = false
  }
}
</script> 