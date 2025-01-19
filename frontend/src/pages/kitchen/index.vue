<template>
  <div class="h-full">
    <div class="mb-8 flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-semibold text-gray-900">Kitchen Dashboard</h1>
        <p class="mt-2 text-sm text-gray-700">
          Manage orders and track kitchen operations
        </p>
      </div>
      
      <div class="flex items-center space-x-4">
        <div class="flex items-center space-x-2">
          <span class="text-sm font-medium text-gray-500">Auto-refresh:</span>
          <button
            class="relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer transition-colors ease-in-out duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            :class="autoRefresh ? 'bg-blue-600' : 'bg-gray-200'"
            @click="toggleAutoRefresh"
          >
            <span
              class="pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200"
              :class="autoRefresh ? 'translate-x-5' : 'translate-x-0'"
            />
          </button>
        </div>
        
        <button
          @click="refreshOrders"
          class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          <svg
            class="h-4 w-4 mr-2"
            :class="{ 'animate-spin': isRefreshing }"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
            />
          </svg>
          Refresh
        </button>
      </div>
    </div>

    <!-- Kitchen Stats -->
    <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8">
      <div
        v-for="stat in stats"
        :key="stat.name"
        class="bg-white overflow-hidden shadow rounded-lg"
      >
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <component
                :is="stat.icon"
                class="h-6 w-6 text-gray-400"
                aria-hidden="true"
              />
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">
                  {{ stat.name }}
                </dt>
                <dd class="flex items-baseline">
                  <div class="text-2xl font-semibold text-gray-900">
                    {{ stat.value }}
                  </div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Queue -->
    <div class="flex-1 min-h-0 bg-white shadow rounded-lg overflow-hidden">
      <OrderQueue
        v-model:orders="orders"
        class="h-[calc(100vh-20rem)]"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useNotificationStore } from '~/stores/notification'
import OrderQueue from '~/components/kitchen/OrderQueue.vue'

const notificationStore = useNotificationStore()
const autoRefresh = ref(true)
const isRefreshing = ref(false)
let refreshInterval: NodeJS.Timeout | null = null

// Mock data for kitchen stats
const stats = ref([
  {
    name: 'Pending Orders',
    value: '12',
    icon: defineComponent({
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>`
    })
  },
  {
    name: 'Average Prep Time',
    value: '18 min',
    icon: defineComponent({
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>`
    })
  },
  {
    name: 'Orders Completed',
    value: '45',
    icon: defineComponent({
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
      </svg>`
    })
  },
  {
    name: 'Active Staff',
    value: '4',
    icon: defineComponent({
      template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>`
    })
  }
])

// Mock orders data
const orders = ref([
  {
    id: '1001',
    status: 'pending',
    priority: 'high',
    items: [
      { id: '1', name: 'Margherita Pizza', quantity: 2, completed: false },
      { id: '2', name: 'Caesar Salad', quantity: 1, notes: 'No croutons', completed: false }
    ],
    orderTime: new Date().toISOString(),
    preparationTime: 20
  },
  {
    id: '1002',
    status: 'preparing',
    priority: 'medium',
    items: [
      { id: '3', name: 'Spaghetti Carbonara', quantity: 1, completed: true },
      { id: '4', name: 'Garlic Bread', quantity: 1, completed: false }
    ],
    orderTime: new Date(Date.now() - 1000 * 60 * 15).toISOString(),
    preparationTime: 15
  },
  {
    id: '1003',
    status: 'ready',
    priority: 'low',
    items: [
      { id: '5', name: 'Chicken Wings', quantity: 3, completed: true },
      { id: '6', name: 'French Fries', quantity: 2, completed: true }
    ],
    orderTime: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    preparationTime: 25
  }
])

const toggleAutoRefresh = () => {
  autoRefresh.value = !autoRefresh.value
  if (autoRefresh.value) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
}

const refreshOrders = async () => {
  isRefreshing.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    // Update orders here
    notificationStore.success('Orders refreshed successfully')
  } catch (error) {
    notificationStore.error('Failed to refresh orders')
  } finally {
    isRefreshing.value = false
  }
}

const startAutoRefresh = () => {
  refreshInterval = setInterval(refreshOrders, 30000) // Refresh every 30 seconds
}

const stopAutoRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
}

onMounted(() => {
  if (autoRefresh.value) {
    startAutoRefresh()
  }
  notificationStore.success('Kitchen dashboard initialized', 'Welcome back!')
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>
