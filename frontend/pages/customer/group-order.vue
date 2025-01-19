<template>
  <div class="min-h-screen bg-white">
    <!-- Header -->
    <div class="bg-white shadow">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold text-gray-900">Group Order</h1>
          <div class="flex items-center space-x-4">
            <div class="bg-orange-100 px-4 py-2 rounded-full">
              <span class="text-[#FF6B00] font-medium">Table #{{ tableNumber }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Group Controller Section -->
      <div v-if="isGroupController" class="mb-8">
        <div class="bg-white shadow sm:rounded-lg">
          <div class="px-4 py-5 sm:p-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900">Share Group Order</h3>
            <div class="mt-4 max-w-xl text-sm text-gray-500">
              <p>Let others join your group order by scanning this QR code or sharing the link.</p>
            </div>
            <div class="mt-5 flex flex-col items-center space-y-4">
              <!-- QR Code -->
              <div class="bg-white p-4 rounded-lg shadow-sm">
                <img :src="qrCodeUrl" alt="Group Order QR Code" class="w-48 h-48" />
              </div>
              
              <!-- Share Link -->
              <div class="flex w-full max-w-md rounded-md shadow-sm">
                <input
                  type="text"
                  :value="shareableLink"
                  readonly
                  class="flex-1 min-w-0 block w-full px-3 py-2 rounded-none rounded-l-md text-sm border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00]"
                />
                <button
                  @click="copyLink"
                  class="inline-flex items-center px-4 py-2 border border-l-0 border-gray-300 rounded-r-md bg-gray-50 hover:bg-gray-100"
                >
                  <ClipboardIcon class="h-5 w-5 text-gray-400" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Group Members -->
      <div class="bg-white shadow sm:rounded-lg mb-8">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg font-medium leading-6 text-gray-900">Group Members</h3>
          <div class="mt-4">
            <ul role="list" class="divide-y divide-gray-200">
              <li v-for="member in groupMembers" :key="member.id" class="py-4 flex items-center justify-between">
                <div class="flex items-center">
                  <UserCircleIcon class="h-8 w-8 text-gray-400" />
                  <div class="ml-3">
                    <p class="text-sm font-medium text-gray-900">{{ member.name }}</p>
                    <p class="text-sm text-gray-500">{{ member.status }}</p>
                  </div>
                </div>
                <div class="text-sm text-gray-500">
                  {{ formatPrice(member.orderTotal) }}
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Order Summary -->
      <div class="bg-white shadow sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg font-medium leading-6 text-gray-900">Order Summary</h3>
          <div class="mt-4">
            <div class="flow-root">
              <ul role="list" class="-my-4 divide-y divide-gray-200">
                <li v-for="item in groupOrder" :key="item.id" class="py-4 flex items-center">
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">{{ item.name }}</p>
                    <p class="text-sm text-gray-500">{{ item.orderedBy }}</p>
                  </div>
                  <div class="ml-4 flex-shrink-0">
                    <span class="text-sm text-gray-500">{{ formatPrice(item.price) }}</span>
                  </div>
                </li>
              </ul>
            </div>
            
            <!-- Total and Actions -->
            <div class="mt-6 border-t border-gray-200 pt-4">
              <div class="flex justify-between text-base font-medium text-gray-900">
                <p>Subtotal</p>
                <p>{{ formatPrice(orderSubtotal) }}</p>
              </div>
              <div class="mt-2 flex justify-between text-sm text-gray-500">
                <p>Tax</p>
                <p>{{ formatPrice(orderTax) }}</p>
              </div>
              <div class="mt-2 flex justify-between text-base font-medium text-gray-900">
                <p>Total</p>
                <p>{{ formatPrice(orderTotal) }}</p>
              </div>
            </div>

            <div class="mt-6 space-y-4">
              <button
                v-if="isGroupController"
                @click="submitGroupOrder"
                :disabled="!canSubmitOrder"
                class="w-full flex justify-center items-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] disabled:bg-gray-300 disabled:cursor-not-allowed"
              >
                Submit Group Order
              </button>
              <button
                v-if="!isGroupController"
                @click="router.push('/customer/menu')"
                class="w-full flex justify-center items-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
              >
                Add Items to Order
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Floating Bell Icon -->
    <button
      @click="hailWaiter"
      :disabled="isHailButtonDisabled"
      class="fixed bottom-6 right-6 bg-[#FF6B00] text-white p-3 rounded-full shadow-lg hover:bg-[#e66000] transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      <BellIcon class="h-6 w-6" />
    </button>

    <!-- Notifications -->
    <div
      v-if="showNotification"
      class="fixed top-4 right-4 left-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded"
      role="alert"
    >
      <p class="font-medium">{{ notificationMessage }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'nuxt/app'
import { UserCircleIcon, ClipboardIcon, BellIcon } from '@heroicons/vue/24/outline'

interface GroupMember {
  id: string
  name: string
  status: string
  orderTotal: number
}

interface OrderItem {
  id: string
  name: string
  price: number
  orderedBy: string
}

const router = useRouter()
const route = useRoute()

const tableNumber = ref(localStorage.getItem('tableNumber') || '')
const isGroupController = ref(false)
const groupMembers = ref<GroupMember[]>([])
const groupOrder = ref<OrderItem[]>([])
const qrCodeUrl = ref('')
const shareableLink = ref('')
const showNotification = ref(false)
const notificationMessage = ref('')
const isHailButtonDisabled = ref(false)

// Computed values for order totals
const orderSubtotal = computed(() => {
  return groupOrder.value.reduce((total, item) => total + item.price, 0)
})

const orderTax = computed(() => orderSubtotal.value * 0.1)
const orderTotal = computed(() => orderSubtotal.value + orderTax.value)

const canSubmitOrder = computed(() => {
  return groupMembers.value.every(member => member.status === 'ready') && groupOrder.value.length > 0
})

onMounted(async () => {
  const groupId = route.query.groupId as string
  
  if (groupId) {
    // Join existing group
    await joinGroup(groupId)
  } else {
    // Create new group
    await createGroup()
  }

  // Initialize WebSocket connection for real-time updates
  initializeWebSocket()
})

const createGroup = async () => {
  try {
    const response = await fetch('/api/group-orders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        tableNumber: tableNumber.value,
        controllerName: localStorage.getItem('guestName'),
      }),
    })

    if (response.ok) {
      const data = await response.json()
      isGroupController.value = true
      qrCodeUrl.value = data.qrCodeUrl
      shareableLink.value = data.shareableLink
    }
  } catch (error) {
    console.error('Error creating group:', error)
  }
}

const joinGroup = async (groupId: string) => {
  try {
    const response = await fetch(`/api/group-orders/${groupId}/join`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: localStorage.getItem('guestName'),
      }),
    })

    if (response.ok) {
      isGroupController.value = false
    }
  } catch (error) {
    console.error('Error joining group:', error)
  }
}

const initializeWebSocket = () => {
  // Initialize WebSocket connection for real-time updates
  const ws = new WebSocket('ws://your-websocket-url')

  ws.onmessage = (event) => {
    const data = JSON.parse(event.data)
    
    if (data.type === 'memberUpdate') {
      groupMembers.value = data.members
    } else if (data.type === 'orderUpdate') {
      groupOrder.value = data.order
    }
  }
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shareableLink.value)
    showNotification.value = true
    notificationMessage.value = 'Link copied to clipboard'
    setTimeout(() => {
      showNotification.value = false
    }, 3000)
  } catch (error) {
    console.error('Error copying link:', error)
  }
}

const submitGroupOrder = async () => {
  try {
    // Store group order data in localStorage for the summary page
    localStorage.setItem('groupOrderData', JSON.stringify({
      members: groupMembers.value,
      order: groupOrder.value
    }))
    
    // Navigate to group order summary page
    router.push('/customer/group-order-summary')
  } catch (error) {
    console.error('Error submitting group order:', error)
  }
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(price)
}

const hailWaiter = async () => {
  try {
    const response = await fetch('/api/hail-waiter', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        tableNumber: tableNumber.value,
      }),
    })

    if (response.ok) {
      isHailButtonDisabled.value = true
      
      // Enable button after 5 minutes
      setTimeout(() => {
        isHailButtonDisabled.value = false
      }, 5 * 60 * 1000)
    }
  } catch (error) {
    console.error('Error hailing waiter:', error)
  }
}
</script> 