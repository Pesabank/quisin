<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="lg:grid lg:grid-cols-2 lg:gap-x-12 xl:gap-x-16">
        <!-- Order Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white shadow-sm rounded-lg">
            <div class="px-4 py-6 sm:px-6">
              <h2 class="text-lg font-medium text-gray-900">Order Summary</h2>
              <div class="mt-6 flow-root">
                <ul role="list" class="-my-6 divide-y divide-gray-200">
                  <li v-for="item in order.items" :key="item.id" class="flex py-6">
                    <div class="h-24 w-24 flex-shrink-0 overflow-hidden rounded-md border border-gray-200">
                      <img
                        :src="item.image"
                        :alt="item.name"
                        class="h-full w-full object-cover object-center"
                      />
                    </div>
                    <div class="ml-4 flex flex-1 flex-col">
                      <div>
                        <div class="flex justify-between text-base font-medium text-gray-900">
                          <h3>{{ item.name }}</h3>
                          <p class="ml-4">${{ (item.price * item.quantity).toFixed(2) }}</p>
                        </div>
                        <p class="mt-1 text-sm text-gray-500">{{ item.description }}</p>
                      </div>
                      <div class="flex flex-1 items-end justify-between text-sm">
                        <p class="text-gray-500">Qty {{ item.quantity }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="border-t border-gray-200 px-4 py-6 sm:px-6">
                <div class="flex justify-between text-base font-medium text-gray-900">
                  <p>Subtotal</p>
                  <p>${{ order.subtotal.toFixed(2) }}</p>
                </div>
                <div class="mt-2 flex justify-between text-sm text-gray-500">
                  <p>Tax (10%)</p>
                  <p>${{ order.tax.toFixed(2) }}</p>
                </div>
                <div class="mt-2 flex justify-between text-base font-medium text-gray-900">
                  <p>Total</p>
                  <p>${{ (order.subtotal + order.tax).toFixed(2) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Payment Details -->
        <div class="mt-10 lg:mt-0 lg:col-span-1">
          <div class="bg-white shadow-sm rounded-lg">
            <div class="px-4 py-6 sm:px-6">
              <h2 class="text-lg font-medium text-gray-900">Payment Method</h2>

              <!-- Payment Method Selection -->
              <div class="mt-6">
                <div class="space-y-4">
                  <div class="relative flex items-start">
                    <div class="flex h-6 items-center">
                      <input
                        id="card"
                        v-model="paymentMethod"
                        value="card"
                        type="radio"
                        class="h-4 w-4 border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                      />
                    </div>
                    <div class="ml-3">
                      <label for="card" class="font-medium text-gray-900">Credit Card</label>
                      <div class="flex space-x-4">
                        <img src="/images/visa.svg" alt="Visa" class="h-8" />
                        <img src="/images/mastercard.svg" alt="Mastercard" class="h-8" />
                        <img src="/images/amex.svg" alt="American Express" class="h-8" />
                      </div>
                    </div>
                  </div>

                  <div class="relative flex items-start">
                    <div class="flex h-6 items-center">
                      <input
                        id="paypal"
                        v-model="paymentMethod"
                        value="paypal"
                        type="radio"
                        class="h-4 w-4 border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                      />
                    </div>
                    <div class="ml-3">
                      <label for="paypal" class="font-medium text-gray-900">PayPal</label>
                      <div class="mt-1">
                        <img src="/images/paypal.svg" alt="PayPal" class="h-8 w-auto" />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Credit Card Form -->
              <div v-if="paymentMethod === 'card'" class="mt-6 space-y-4">
                <div>
                  <label for="card-number" class="block text-sm font-medium text-gray-700">
                    Card Number
                  </label>
                  <div class="mt-1">
                    <input
                      type="text"
                      id="card-number"
                      v-model="cardDetails.number"
                      maxlength="19"
                      @input="formatCardNumber"
                      class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                      placeholder="0000 0000 0000 0000"
                    />
                  </div>
                </div>

                <div class="grid grid-cols-3 gap-4">
                  <div class="col-span-2">
                    <label for="expiry" class="block text-sm font-medium text-gray-700">
                      Expiry Date
                    </label>
                    <div class="mt-1">
                      <input
                        type="text"
                        id="expiry"
                        v-model="cardDetails.expiry"
                        maxlength="5"
                        @input="formatExpiry"
                        class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        placeholder="MM/YY"
                      />
                    </div>
                  </div>

                  <div>
                    <label for="cvc" class="block text-sm font-medium text-gray-700">
                      CVC
                    </label>
                    <div class="mt-1">
                      <input
                        type="text"
                        id="cvc"
                        v-model="cardDetails.cvc"
                        maxlength="4"
                        class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        placeholder="123"
                      />
                    </div>
                  </div>
                </div>

                <div>
                  <label for="name" class="block text-sm font-medium text-gray-700">
                    Name on Card
                  </label>
                  <div class="mt-1">
                    <input
                      type="text"
                      id="name"
                      v-model="cardDetails.name"
                      class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                      placeholder="John Doe"
                    />
                  </div>
                </div>
              </div>

              <!-- PayPal Section -->
              <div v-if="paymentMethod === 'paypal'" class="mt-6">
                <p class="text-sm text-gray-500">
                  You will be redirected to PayPal to complete your payment securely.
                </p>
              </div>

              <!-- Billing Address -->
              <div class="mt-6">
                <h3 class="text-base font-medium text-gray-900">Billing Address</h3>
                <div class="mt-4 space-y-4">
                  <div>
                    <label for="address" class="block text-sm font-medium text-gray-700">
                      Street Address
                    </label>
                    <div class="mt-1">
                      <input
                        type="text"
                        id="address"
                        v-model="billingAddress.street"
                        class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                      />
                    </div>
                  </div>

                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <label for="city" class="block text-sm font-medium text-gray-700">
                        City
                      </label>
                      <div class="mt-1">
                        <input
                          type="text"
                          id="city"
                          v-model="billingAddress.city"
                          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        />
                      </div>
                    </div>

                    <div>
                      <label for="postal-code" class="block text-sm font-medium text-gray-700">
                        Postal Code
                      </label>
                      <div class="mt-1">
                        <input
                          type="text"
                          id="postal-code"
                          v-model="billingAddress.postalCode"
                          class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Terms and Conditions -->
              <div class="mt-6">
                <div class="relative flex items-start">
                  <div class="flex h-6 items-center">
                    <input
                      id="terms"
                      v-model="acceptedTerms"
                      type="checkbox"
                      class="h-4 w-4 rounded border-gray-300 text-[#FF6B00] focus:ring-[#FF6B00]"
                    />
                  </div>
                  <div class="ml-3">
                    <label for="terms" class="text-sm text-gray-500">
                      I agree to the
                      <a href="#" class="font-medium text-[#FF6B00] hover:text-[#e66000]">
                        terms and conditions
                      </a>
                    </label>
                  </div>
                </div>
              </div>

              <!-- Pay Button -->
              <div class="mt-6">
                <button
                  @click="processPayment"
                  :disabled="!isFormValid"
                  :class="[
                    'w-full bg-[#FF6B00] text-white px-6 py-3 rounded-md hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]'
                  ]"
                >
                  Pay ${{ (order.subtotal + order.tax).toFixed(2) }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Payment Success Modal -->
    <TransitionRoot as="template" :show="showSuccessModal">
      <Dialog as="div" class="relative z-10" @close="closeSuccessModal">
        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 z-10 overflow-y-auto">
          <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <TransitionChild
              as="template"
              enter="ease-out duration-300"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="ease-in duration-200"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            >
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-sm sm:p-6">
                <div>
                  <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
                    <CheckIcon class="h-6 w-6 text-green-600" aria-hidden="true" />
                  </div>
                  <div class="mt-3 text-center sm:mt-5">
                    <DialogTitle as="h3" class="text-base font-semibold leading-6 text-gray-900">
                      Payment Successful
                    </DialogTitle>
                    <div class="mt-2">
                      <p class="text-sm text-gray-500">
                        Your order has been placed successfully. You will receive a confirmation email shortly.
                      </p>
                    </div>
                  </div>
                </div>
                <div class="mt-5 sm:mt-6">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-[#FF6B00] px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-[#e66000] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#FF6B00]"
                    @click="goToOrders"
                  >
                    View My Orders
                  </button>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'nuxt/app'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { CheckIcon } from '@heroicons/vue/24/outline'

interface OrderItem {
  id: string
  name: string
  description: string
  price: number
  quantity: number
  image: string
}

interface Order {
  items: OrderItem[]
  subtotal: number
  tax: number
}

// Mock order data
const order = ref<Order>({
  items: [
    {
      id: '1',
      name: 'Margherita Pizza',
      description: 'Fresh tomatoes, mozzarella, basil, and olive oil',
      price: 14.99,
      quantity: 1,
      image: 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002'
    },
    {
      id: '2',
      name: 'Spicy Chicken Wings',
      description: 'Crispy wings tossed in our signature hot sauce',
      price: 12.99,
      quantity: 2,
      image: 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f'
    }
  ],
  subtotal: 40.97,
  tax: 4.10
})

// State
const router = useRouter()
const paymentMethod = ref('card')
const showSuccessModal = ref(false)
const acceptedTerms = ref(false)

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
  if (!acceptedTerms.value) return false

  if (paymentMethod.value === 'card') {
    return (
      cardDetails.value.number.replace(/\s/g, '').length === 16 &&
      cardDetails.value.expiry.length === 5 &&
      cardDetails.value.cvc.length >= 3 &&
      cardDetails.value.name.length > 0 &&
      billingAddress.value.street.length > 0 &&
      billingAddress.value.city.length > 0 &&
      billingAddress.value.postalCode.length > 0
    )
  }

  return true
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

const processPayment = async () => {
  try {
    // TODO: Implement payment processing
    console.log('Processing payment...')
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    showSuccessModal.value = true
  } catch (error) {
    console.error('Payment failed:', error)
  }
}

const closeSuccessModal = () => {
  showSuccessModal.value = false
}

const goToOrders = () => {
  router.push('/customer/orders')
}
</script> 