<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Orders Management</h1>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex justify-between mb-4">
            <div class="flex space-x-4">
              <select
                v-model="filterStatus"
                class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              >
                <option value="all">All Status</option>
                <option value="pending">Pending</option>
                <option value="preparing">Preparing</option>
                <option value="ready">Ready</option>
                <option value="delivered">Delivered</option>
                <option value="cancelled">Cancelled</option>
              </select>
              <input
                type="text"
                v-model="searchQuery"
                placeholder="Search orders..."
                class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              />
            </div>
            <button
              @click="refreshOrders"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
            >
              Refresh
            </button>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Order ID
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Table
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Items
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Total
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Time
                  </th>
                  <th scope="col" class="relative px-6 py-3">
                    <span class="sr-only">Actions</span>
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="order in filteredOrders" :key="order.id">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    #{{ order.id }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    Table {{ order.table }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                      :class="statusClasses[order.status]"
                    >
                      {{ order.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ order.items.length }} items
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ${{ order.total.toFixed(2) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatTime(order.time) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      @click="viewOrderDetails(order)"
                      class="text-blue-600 hover:text-blue-900 mr-4"
                    >
                      View
                    </button>
                    <button
                      v-if="order.status === 'pending'"
                      @click="cancelOrder(order)"
                      class="text-red-600 hover:text-red-900"
                    >
                      Cancel
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface OrderItem {
  id: string
  name: string
  quantity: number
  price: number
  notes?: string
}

interface Order {
  id: string
  table: number
  status: 'pending' | 'preparing' | 'ready' | 'delivered' | 'cancelled'
  items: OrderItem[]
  total: number
  time: string
}

const orders = ref<Order[]>([
  {
    id: '1001',
    table: 5,
    status: 'pending',
    items: [
      { id: '1', name: 'Margherita Pizza', quantity: 2, price: 12.99 },
      { id: '2', name: 'Caesar Salad', quantity: 1, price: 8.99, notes: 'No croutons' }
    ],
    total: 34.97,
    time: new Date().toISOString()
  },
  {
    id: '1002',
    table: 3,
    status: 'preparing',
    items: [
      { id: '3', name: 'Spaghetti Carbonara', quantity: 1, price: 14.99 },
      { id: '4', name: 'Garlic Bread', quantity: 1, price: 4.99 }
    ],
    total: 19.98,
    time: new Date(Date.now() - 1000 * 60 * 15).toISOString()
  }
])

const filterStatus = ref('all')
const searchQuery = ref('')

const statusClasses = {
  pending: 'bg-yellow-100 text-yellow-800',
  preparing: 'bg-blue-100 text-blue-800',
  ready: 'bg-green-100 text-green-800',
  delivered: 'bg-gray-100 text-gray-800',
  cancelled: 'bg-red-100 text-red-800'
}

const filteredOrders = computed(() => {
  let result = orders.value

  if (filterStatus.value !== 'all') {
    result = result.filter(order => order.status === filterStatus.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(order => 
      order.id.toLowerCase().includes(query) ||
      order.items.some(item => item.name.toLowerCase().includes(query))
    )
  }

  return result
})

const formatTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const refreshOrders = async () => {
  try {
    // TODO: Implement API call to refresh orders
    console.log('Refreshing orders...')
  } catch (error) {
    console.error('Error refreshing orders:', error)
  }
}

const viewOrderDetails = (order: Order) => {
  // TODO: Implement order details view
  console.log('Viewing order:', order.id)
}

const cancelOrder = async (order: Order) => {
  if (confirm(`Are you sure you want to cancel order #${order.id}?`)) {
    try {
      // TODO: Implement API call to cancel order
      order.status = 'cancelled'
    } catch (error) {
      console.error('Error cancelling order:', error)
    }
  }
}
</script> 