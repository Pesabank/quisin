<template>
  <div class="min-h-screen bg-white flex flex-col items-center justify-center px-4">
    <!-- Logo and Welcome Message -->
    <div class="text-center mb-8">
      <h1 class="text-[#FF6B00] text-4xl font-bold mb-2">Quisin</h1>
      <h2 class="text-2xl font-semibold text-gray-900">Group Order</h2>
      <p class="mt-2 text-lg text-gray-600">Share with your friends to join the order</p>
    </div>

    <!-- QR Code Section -->
    <div class="bg-white shadow-lg rounded-lg p-8 max-w-md w-full">
      <div class="flex flex-col items-center space-y-6">
        <!-- QR Code -->
        <div class="bg-white p-4 rounded-lg shadow-sm">
          <img :src="qrCodeUrl" alt="Group Order QR Code" class="w-64 h-64" />
        </div>
        
        <!-- Share Link -->
        <div class="w-full">
          <label class="block text-sm font-medium text-gray-700 mb-2">Share Link</label>
          <div class="flex rounded-md shadow-sm">
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

        <!-- Group Members -->
        <div class="w-full">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Group Members ({{ groupMembers.length }})</h3>
          <ul role="list" class="divide-y divide-gray-200 max-h-48 overflow-y-auto">
            <li v-for="member in groupMembers" :key="member.id" class="py-4 flex items-center">
              <UserCircleIcon class="h-8 w-8 text-gray-400" />
              <div class="ml-3">
                <p class="text-sm font-medium text-gray-900">{{ member.name }}</p>
                <p class="text-xs text-gray-500">{{ member.status }}</p>
              </div>
            </li>
          </ul>
        </div>

        <!-- Continue Button -->
        <button
          @click="continueToOrder"
          class="w-full bg-[#FF6B00] text-white px-6 py-3 rounded-lg text-lg font-medium hover:bg-[#e66000] transition-colors duration-200"
        >
          Continue to Order
        </button>
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

    <!-- Notification -->
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'nuxt/app'
import { UserCircleIcon, ClipboardIcon, BellIcon } from '@heroicons/vue/24/outline'

interface GroupMember {
  id: string
  name: string
  status: string
}

const router = useRouter()
const qrCodeUrl = ref('')
const shareableLink = ref('')
const groupMembers = ref<GroupMember[]>([])
const showNotification = ref(false)
const notificationMessage = ref('')
const isHailButtonDisabled = ref(false)

onMounted(async () => {
  // Create group and get QR code
  await createGroup()

  // Initialize WebSocket connection for real-time member updates
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
        tableNumber: localStorage.getItem('tableNumber'),
        controllerName: localStorage.getItem('guestName'),
      }),
    })

    if (response.ok) {
      const data = await response.json()
      qrCodeUrl.value = data.qrCodeUrl
      shareableLink.value = data.shareableLink
      groupMembers.value = [{ 
        id: data.controllerId, 
        name: localStorage.getItem('guestName') || 'You', 
        status: 'Group Leader' 
      }]
    }
  } catch (error) {
    console.error('Error creating group:', error)
  }
}

const initializeWebSocket = () => {
  const ws = new WebSocket('ws://your-websocket-url')

  ws.onmessage = (event) => {
    const data = JSON.parse(event.data)
    if (data.type === 'memberUpdate') {
      groupMembers.value = data.members
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

const continueToOrder = () => {
  router.push('/customer/group-order')
}

const hailWaiter = async () => {
  try {
    const response = await fetch('/api/hail-waiter', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        tableNumber: localStorage.getItem('tableNumber'),
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