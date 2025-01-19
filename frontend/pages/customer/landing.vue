<template>
  <div class="min-h-screen bg-white flex flex-col items-center justify-center px-4">
    <!-- Logo and Welcome Message -->
    <div class="text-center mb-8">
      <h1 class="text-[#FF6B00] text-4xl font-bold mb-2">Quisin</h1>
      <h2 class="text-2xl font-semibold text-gray-900">Welcome to {{ restaurantName }}</h2>
      <p class="mt-2 text-xl font-medium text-gray-600">Table #{{ tableNumber }}</p>
    </div>

    <!-- Action Buttons -->
    <div class="space-y-4 w-full max-w-xs">
      <button
        @click="hailWaiter"
        class="w-full bg-[#FF6B00] text-white px-6 py-4 rounded-lg text-lg font-medium hover:bg-[#e66000] transition-colors duration-200"
        :disabled="isHailButtonDisabled"
      >
        Hail Waiter
      </button>
      <button
        @click="navigateToMenu"
        class="w-full bg-white text-[#FF6B00] border-2 border-[#FF6B00] px-6 py-4 rounded-lg text-lg font-medium hover:bg-orange-50 transition-colors duration-200"
      >
        Continue to Menu
      </button>
    </div>

    <!-- Notification -->
    <TransitionRoot as="template" :show="showNotification">
      <div
        class="fixed top-4 right-4 left-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded transition-all duration-1000 ease-in-out"
        role="alert"
      >
        <div class="flex items-center">
          <div class="loading-dots" v-if="isWaiting">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <p class="font-medium ml-2">{{ notificationMessage }}</p>
        </div>
      </div>
    </TransitionRoot>
  </div>
</template>

<style scoped>
.loading-dots {
  display: flex;
  align-items: center;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  margin: 0 4px;
  background-color: #15803d;
  border-radius: 50%;
  display: inline-block;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}
</style>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'nuxt/app'

const router = useRouter()
const route = useRoute()

const restaurantName = ref('')
const tableNumber = ref('')
const showNotification = ref(false)
const notificationMessage = ref('')
const isHailButtonDisabled = ref(false)
const isWaiting = ref(false)

onMounted(() => {
  // Get restaurant name and table number from QR code parameters
  restaurantName.value = route.query.restaurant as string || 'Restaurant'
  tableNumber.value = route.query.table as string || ''
})

const hailWaiter = async () => {
  try {
    isWaiting.value = true
    notificationMessage.value = 'Hailing waiter...'
    showNotification.value = true
    isHailButtonDisabled.value = true

    // Call backend API to hail waiter
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
      const data = await response.json()
      setTimeout(() => {
        isWaiting.value = false
        notificationMessage.value = `Your waiter ${data.waiterName} is on the way`
      }, 2000)

      // Enable button after 5 minutes
      setTimeout(() => {
        isHailButtonDisabled.value = false
        showNotification.value = false
      }, 5 * 60 * 1000)
    }
  } catch (error) {
    console.error('Error hailing waiter:', error)
    isWaiting.value = false
    showNotification.value = false
  }
}

const navigateToMenu = () => {
  router.push('/customer/order-type')
}
</script> 