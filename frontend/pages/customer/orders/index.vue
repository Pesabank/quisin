<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <h1 class="text-2xl font-bold text-gray-900 mb-8">My Orders</h1>

      <!-- Orders List -->
      <div class="space-y-6">
        <div v-for="order in orders" :key="order.id" class="bg-white shadow-sm rounded-lg overflow-hidden">
          <!-- Order Header -->
          <div class="px-4 py-5 sm:px-6 bg-gray-50 border-b border-gray-200">
            <div class="flex justify-between items-start">
              <div>
                <h3 class="text-lg font-medium text-gray-900">
                  Order #{{ order.id }}
                  <span v-if="order.type === 'group'" class="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    Group Order
                  </span>
                </h3>
                <p class="mt-1 text-sm text-gray-500">
                  {{ new Date(order.createdAt).toLocaleString() }}
                </p>
              </div>
              <div class="flex items-center space-x-4">
                <span :class="[
                  'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium',
                  order.status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
                  order.status === 'paid' ? 'bg-green-100 text-green-800' :
                  'bg-gray-100 text-gray-800'
                ]">
                  {{ order.status.charAt(0).toUpperCase() + order.status.slice(1) }}
                </span>
                <button
                  v-if="order.status === 'pending'"
                  @click="proceedToPayment(order)"
                  class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                >
                  Pay for Order
                </button>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div class="px-4 py-5 sm:px-6">
            <div v-if="order.type === 'group'">
              <!-- Group Order Items -->
              <div v-for="member in order.members" :key="member.id" class="mb-6">
                <div class="flex items-center mb-3">
                  <UserCircleIcon class="h-5 w-5 text-gray-400 mr-2" />
                  <h4 class="text-sm font-medium text-gray-900">{{ member.name }}</h4>
                </div>
                <ul role="list" class="divide-y divide-gray-200">
                  <li v-for="item in member.items" :key="item.id" class="py-3 flex">
                    <img :src="item.image" :alt="item.name" class="h-16 w-16 rounded-md object-cover" />
                    <div class="ml-4 flex-1">
                      <div class="flex justify-between">
                        <div>
                          <p class="text-sm font-medium text-gray-900">{{ item.name }}</p>
                          <p class="mt-1 text-sm text-gray-500">Qty: {{ item.quantity }}</p>
                        </div>
                        <p class="text-sm font-medium text-gray-900">{{ formatPrice(item.price * item.quantity) }}</p>
                      </div>
                      <p v-if="item.comments" class="mt-1 text-sm text-gray-500 italic">"{{ item.comments }}"</p>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div v-else>
              <!-- Single Order Items -->
              <ul role="list" class="divide-y divide-gray-200">
                <li v-for="item in order.items" :key="item.id" class="py-3 flex">
                  <img :src="item.image" :alt="item.name" class="h-16 w-16 rounded-md object-cover" />
                  <div class="ml-4 flex-1">
                    <div class="flex justify-between">
                      <div>
                        <p class="text-sm font-medium text-gray-900">{{ item.name }}</p>
                        <p class="mt-1 text-sm text-gray-500">Qty: {{ item.quantity }}</p>
                      </div>
                      <p class="text-sm font-medium text-gray-900">{{ formatPrice(item.price * item.quantity) }}</p>
                    </div>
                    <p v-if="item.comments" class="mt-1 text-sm text-gray-500 italic">"{{ item.comments }}"</p>
                  </div>
                </li>
              </ul>
            </div>

            <!-- Order Summary -->
            <div class="mt-6 border-t border-gray-200 pt-6">
              <div class="flex justify-between text-sm font-medium text-gray-900">
                <p>Subtotal</p>
                <p>{{ formatPrice(order.subtotal) }}</p>
              </div>
              <div class="mt-2 flex justify-between text-sm text-gray-500">
                <p>Tax (10%)</p>
                <p>{{ formatPrice(order.tax) }}</p>
              </div>
              <div class="mt-2 flex justify-between text-base font-medium text-gray-900">
                <p>Total</p>
                <p>{{ formatPrice(order.total) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- No Orders Message -->
      <div v-if="orders.length === 0" class="text-center py-12">
        <p class="text-gray-500">No orders found.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'nuxt/app'
import { UserCircleIcon } from '@heroicons/vue/24/outline'

interface OrderItem {
  id: string
  name: string
  price: number
  quantity: number
  image: string
  comments?: string
}

interface GroupMember {
  id: string
  name: string
  status: string
  items: OrderItem[]
}

interface Order {
  id: string
  items?: OrderItem[]
  members?: GroupMember[]
  subtotal: number
  tax: number
  total: number
  status: 'pending' | 'paid' | 'completed'
  tableNumber: string
  customerName?: string
  groupId?: string
  createdAt: string
  type?: 'single' | 'group'
}

const router = useRouter()
const orders = ref<Order[]>([])

onMounted(() => {
  // Get orders from localStorage
  const storedOrders = localStorage.getItem('orders')
  if (storedOrders) {
    orders.value = JSON.parse(storedOrders)
    // Sort orders by date (newest first)
    orders.value.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }
})

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-KE', {
    style: 'currency',
    currency: 'KES'
  }).format(price)
}

const proceedToPayment = (order: Order) => {
  // Store current order ID for payment reference
  if (order.type === 'group') {
    localStorage.setItem('currentGroupOrderId', order.id)
    router.push('/customer/group-order-checkout')
  } else {
    localStorage.setItem('currentOrderId', order.id)
    router.push('/customer/checkout')
  }
}
</script> 