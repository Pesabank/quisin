<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Reports & Analytics</h1>
          <div class="flex items-center space-x-4">
            <select
              v-model="selectedPeriod"
              class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            >
              <option value="today">Today</option>
              <option value="week">This Week</option>
              <option value="month">This Month</option>
              <option value="year">This Year</option>
            </select>
            <button
              @click="refreshData"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
            >
              Refresh Data
            </button>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Overview Stats -->
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
                  <dt class="text-sm font-medium text-gray-500 truncate">{{ stat.name }}</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ stat.value }}</div>
                    <div
                      v-if="stat.change"
                      :class="[
                        stat.change.type === 'increase' ? 'text-green-600' : 'text-red-600',
                        'ml-2 text-sm font-medium'
                      ]"
                    >
                      {{ stat.change.value }}
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts -->
      <div class="grid grid-cols-1 gap-5 lg:grid-cols-2">
        <!-- Revenue Chart -->
        <div class="bg-white shadow rounded-lg p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Revenue Trend</h3>
          <div class="h-80">
            <!-- TODO: Implement revenue chart -->
            <div class="flex items-center justify-center h-full text-gray-500">
              Revenue Chart Placeholder
            </div>
          </div>
        </div>

        <!-- Orders Chart -->
        <div class="bg-white shadow rounded-lg p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Orders Overview</h3>
          <div class="h-80">
            <!-- TODO: Implement orders chart -->
            <div class="flex items-center justify-center h-full text-gray-500">
              Orders Chart Placeholder
            </div>
          </div>
        </div>
      </div>

      <!-- Popular Items -->
      <div class="mt-8 bg-white shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Popular Items</h3>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Item
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Orders
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Revenue
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Rating
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="item in popularItems" :key="item.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm font-medium text-gray-900">{{ item.name }}</div>
                    <div class="text-sm text-gray-500">{{ item.category }}</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ item.orders }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ${{ item.revenue.toFixed(2) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <span class="text-sm text-gray-900 mr-2">{{ item.rating }}</span>
                      <div class="flex text-yellow-400">
                        <svg v-for="i in 5" :key="i" class="h-5 w-5" :class="i <= item.rating ? 'text-yellow-400' : 'text-gray-200'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      </div>
                    </div>
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
import { ref, defineComponent } from 'vue'

const selectedPeriod = ref('week')

interface Stat {
  name: string
  value: string
  icon: any
  change?: {
    value: string
    type: 'increase' | 'decrease'
  }
}

const stats = ref<Stat[]>([
  {
    name: 'Total Revenue',
    value: '$45,231',
    change: { value: '+20.1%', type: 'increase' },
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      `
    })
  },
  {
    name: 'Total Orders',
    value: '356',
    change: { value: '+12.5%', type: 'increase' },
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
      `
    })
  },
  {
    name: 'Average Order Value',
    value: '$127.05',
    change: { value: '+5.2%', type: 'increase' },
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
        </svg>
      `
    })
  },
  {
    name: 'Customer Satisfaction',
    value: '4.8/5',
    change: { value: '+0.3', type: 'increase' },
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      `
    })
  }
])

interface PopularItem {
  id: string
  name: string
  category: string
  orders: number
  revenue: number
  rating: number
}

const popularItems = ref<PopularItem[]>([
  {
    id: '1',
    name: 'Margherita Pizza',
    category: 'Pizza',
    orders: 245,
    revenue: 3185.55,
    rating: 4.8
  },
  {
    id: '2',
    name: 'Caesar Salad',
    category: 'Salads',
    orders: 189,
    revenue: 1701.00,
    rating: 4.5
  },
  {
    id: '3',
    name: 'Spaghetti Carbonara',
    category: 'Pasta',
    orders: 156,
    revenue: 2339.44,
    rating: 4.7
  }
])

const refreshData = async () => {
  try {
    // TODO: Implement API calls to refresh analytics data
    console.log('Refreshing analytics data...')
  } catch (error) {
    console.error('Error refreshing data:', error)
  }
}
</script> 