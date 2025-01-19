<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-white shadow-sm rounded-lg">
        <!-- Header -->
        <div class="px-4 py-6 sm:px-6 border-b border-gray-200">
          <h1 class="text-2xl font-bold text-gray-900">Order Summary</h1>
          <p class="mt-1 text-sm text-gray-500">Please review your order before placing it.</p>
        </div>

        <!-- Order Items -->
        <div class="px-4 py-6 sm:px-6">
          <ul role="list" class="divide-y divide-gray-200">
            <li v-for="item in cartItems" :key="item.id" class="py-6 flex">
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
                    <p class="ml-4">{{ formatPrice(item.price * item.quantity) }}</p>
                  </div>
                  <p class="mt-1 text-sm text-gray-500">Quantity: {{ item.quantity }}</p>
                </div>
                <div v-if="item.comments" class="mt-2">
                  <p class="text-sm text-gray-500 italic">"{{ item.comments }}"</p>
                </div>
              </div>
            </li>
          </ul>
        </div>

        <!-- Order Summary -->
        <div class="px-4 py-6 sm:px-6 border-t border-gray-200">
          <div class="flex justify-between text-base font-medium text-gray-900">
            <p>Subtotal</p>
            <p>{{ formatPrice(subtotal) }}</p>
          </div>
          <div class="mt-2 flex justify-between text-sm text-gray-500">
            <p>Tax (10%)</p>
            <p>{{ formatPrice(tax) }}</p>
          </div>
          <div class="mt-2 flex justify-between text-lg font-medium text-gray-900">
            <p>Total</p>
            <p>{{ formatPrice(total) }}</p>
          </div>

          <!-- Action Buttons -->
          <div class="mt-6 space-y-4">
            <button
              @click="placeOrder"
              :disabled="isLoading"
              class="w-full flex justify-center items-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg
                v-if="isLoading"
                class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                ></circle>
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
              {{ isLoading ? 'Placing Order...' : 'Place Order' }}
            </button>
            <button
              @click="router.back()"
              class="w-full flex justify-center items-center px-6 py-3 border-2 border-[#FF6B00] rounded-md text-base font-medium text-[#FF6B00] bg-white hover:bg-orange-50"
            >
              Modify Order
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Success Modal -->
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
                      Order Placed Successfully
                    </DialogTitle>
                    <div class="mt-2">
                      <p class="text-sm text-gray-500">
                        Your order has been placed successfully. You can view your order status or proceed to payment.
                      </p>
                    </div>
                  </div>
                </div>
                <div class="mt-5 sm:mt-6 space-y-3">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-[#FF6B00] px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-[#e66000]"
                    @click="viewOrders"
                  >
                    View My Orders
                  </button>
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-[#FF6B00] shadow-sm ring-1 ring-inset ring-[#FF6B00] hover:bg-orange-50"
                    @click="proceedToPayment"
                  >
                    Pay for Order
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'nuxt/app'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { CheckIcon } from '@heroicons/vue/24/outline'

interface CartItem {
  id: string
  name: string
  price: number
  quantity: number
  image: string
  comments?: string
}

const router = useRouter()
const cartItems = ref<CartItem[]>([])
const showSuccessModal = ref(false)
const isLoading = ref(false)

onMounted(() => {
  // Get cart items from localStorage
  const storedCart = localStorage.getItem('cartItems')
  if (storedCart) {
    cartItems.value = JSON.parse(storedCart)
  }
})

const subtotal = computed(() => {
  return cartItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
})

const tax = computed(() => subtotal.value * 0.1)
const total = computed(() => subtotal.value + tax.value)

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-KE', {
    style: 'currency',
    currency: 'KES'
  }).format(price)
}

const placeOrder = async () => {
  if (isLoading.value) return
  isLoading.value = true

  try {
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000))

    // Simulate successful order creation
    const orderId = `ORD-${Date.now()}`
    localStorage.setItem('currentOrderId', orderId)
    
    // Store order details for the orders page
    const orderData = {
      id: orderId,
      items: cartItems.value,
      subtotal: subtotal.value,
      tax: tax.value,
      total: total.value,
      status: 'pending',
      tableNumber: localStorage.getItem('tableNumber'),
      customerName: localStorage.getItem('guestName') || 'Guest',
      createdAt: new Date().toISOString()
    }
    
    // Store order in localStorage
    const existingOrders = JSON.parse(localStorage.getItem('orders') || '[]')
    existingOrders.push(orderData)
    localStorage.setItem('orders', JSON.stringify(existingOrders))
    
    // Clear cart after successful order
    localStorage.removeItem('cartItems')
    showSuccessModal.value = true
  } catch (error) {
    console.error('Error placing order:', error)
    alert('Failed to place order. Please try again.')
  } finally {
    isLoading.value = false
  }
}

const closeSuccessModal = () => {
  showSuccessModal.value = false
}

const viewOrders = () => {
  router.push('/customer/orders')
}

const proceedToPayment = () => {
  router.push('/customer/checkout')
}
</script> 