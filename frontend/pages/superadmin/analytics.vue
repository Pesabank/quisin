<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center sm:justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Analytics Dashboard</h1>
          <p class="mt-2 text-sm text-gray-700">
            Comprehensive analytics across all restaurants
          </p>
        </div>
        <div class="mt-4 sm:mt-0 flex space-x-3">
          <!-- Time Period Selector -->
          <select
            v-model="selectedPeriod"
            class="block rounded-md border-gray-300 py-2 pl-3 pr-10 text-base focus:border-[#FF6B00] focus:outline-none focus:ring-[#FF6B00] sm:text-sm"
          >
            <option value="today">Today</option>
            <option value="week">This Week</option>
            <option value="month">This Month</option>
            <option value="year">This Year</option>
          </select>

          <!-- Export Button -->
          <button
            @click="exportAnalytics"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <ArrowDownTrayIcon class="h-5 w-5 mr-2" />
            Export Analytics
          </button>
        </div>
      </div>

      <!-- Key Metrics -->
      <div class="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CurrencyDollarIcon class="h-6 w-6 text-[#FF6B00]" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Revenue</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">$2,458,900</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-green-600">
                      <ArrowUpIcon class="self-center flex-shrink-0 h-5 w-5" />
                      <span class="sr-only">Increased by</span>
                      12.5%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <ShoppingCartIcon class="h-6 w-6 text-[#FF6B00]" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Orders</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">45,678</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-green-600">
                      <ArrowUpIcon class="self-center flex-shrink-0 h-5 w-5" />
                      <span class="sr-only">Increased by</span>
                      8.2%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <UsersIcon class="h-6 w-6 text-[#FF6B00]" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Active Users</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">12,345</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-green-600">
                      <ArrowUpIcon class="self-center flex-shrink-0 h-5 w-5" />
                      <span class="sr-only">Increased by</span>
                      3.2%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <ChartBarIcon class="h-6 w-6 text-[#FF6B00]" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Average Order Value</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">$54.32</div>
                    <div class="ml-2 flex items-baseline text-sm font-semibold text-red-600">
                      <ArrowDownIcon class="self-center flex-shrink-0 h-5 w-5" />
                      <span class="sr-only">Decreased by</span>
                      1.5%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="mt-8 grid grid-cols-1 gap-8 lg:grid-cols-2">
        <!-- Revenue Chart -->
        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-medium text-gray-900">Revenue Trends</h2>
            <div class="flex items-center space-x-3">
              <select
                v-model="revenueChartType"
                class="block rounded-md border-gray-300 py-1.5 pl-3 pr-10 text-base focus:border-[#FF6B00] focus:outline-none focus:ring-[#FF6B00] sm:text-sm"
              >
                <option value="daily">Daily</option>
                <option value="weekly">Weekly</option>
                <option value="monthly">Monthly</option>
              </select>
            </div>
          </div>
          <div class="mt-6 h-80">
            <!-- Chart will be rendered here -->
            <div class="w-full h-full flex items-center justify-center text-gray-500">
              Revenue Chart Placeholder
            </div>
          </div>
        </div>

        <!-- Orders Chart -->
        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-medium text-gray-900">Order Statistics</h2>
            <div class="flex items-center space-x-3">
              <select
                v-model="orderChartType"
                class="block rounded-md border-gray-300 py-1.5 pl-3 pr-10 text-base focus:border-[#FF6B00] focus:outline-none focus:ring-[#FF6B00] sm:text-sm"
              >
                <option value="daily">Daily</option>
                <option value="weekly">Weekly</option>
                <option value="monthly">Monthly</option>
              </select>
            </div>
          </div>
          <div class="mt-6 h-80">
            <!-- Chart will be rendered here -->
            <div class="w-full h-full flex items-center justify-center text-gray-500">
              Orders Chart Placeholder
            </div>
          </div>
        </div>
      </div>

      <!-- Restaurant Performance -->
      <div class="mt-8">
        <div class="sm:flex sm:items-center">
          <div class="sm:flex-auto">
            <h2 class="text-lg font-medium text-gray-900">Restaurant Performance</h2>
            <p class="mt-2 text-sm text-gray-700">
              Detailed performance metrics for all restaurants
            </p>
          </div>
          <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
            <div class="flex items-center space-x-3">
              <select
                v-model="sortBy"
                class="block rounded-md border-gray-300 py-1.5 pl-3 pr-10 text-base focus:border-[#FF6B00] focus:outline-none focus:ring-[#FF6B00] sm:text-sm"
              >
                <option value="revenue">Revenue</option>
                <option value="orders">Orders</option>
                <option value="rating">Rating</option>
                <option value="growth">Growth</option>
              </select>
            </div>
          </div>
        </div>
        <div class="mt-6 overflow-hidden shadow ring-1 ring-black ring-opacity-5 sm:rounded-lg">
          <table class="min-w-full divide-y divide-gray-300">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                  Restaurant
                </th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Revenue
                </th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Orders
                </th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Avg. Order Value
                </th>
                <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Growth
                </th>
                <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                  <span class="sr-only">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr v-for="restaurant in sortedRestaurants" :key="restaurant.id">
                <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-6">
                  {{ restaurant.name }}
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                  {{ formatCurrency(restaurant.revenue) }}
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                  {{ restaurant.orders }}
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                  {{ formatCurrency(restaurant.avgOrderValue) }}
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm">
                  <span
                    :class="[
                      restaurant.growth >= 0 ? 'text-green-600' : 'text-red-600',
                      'inline-flex items-center'
                    ]"
                  >
                    <component
                      :is="restaurant.growth >= 0 ? ArrowUpIcon : ArrowDownIcon"
                      class="h-4 w-4 mr-1"
                    />
                    {{ Math.abs(restaurant.growth) }}%
                  </span>
                </td>
                <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                  <button
                    @click="viewDetails(restaurant)"
                    class="text-[#FF6B00] hover:text-[#e66000]"
                  >
                    View Details
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  ArrowDownIcon,
  ArrowUpIcon,
  ArrowDownTrayIcon,
  ChartBarIcon,
  CurrencyDollarIcon,
  ShoppingCartIcon,
  UsersIcon
} from '@heroicons/vue/24/outline'

interface Restaurant {
  id: string
  name: string
  revenue: number
  orders: number
  avgOrderValue: number
  growth: number
}

const selectedPeriod = ref('month')
const revenueChartType = ref('monthly')
const orderChartType = ref('monthly')
const sortBy = ref('revenue')

// Mock data
const restaurants = ref<Restaurant[]>([
  {
    id: '1',
    name: 'Restaurant A',
    revenue: 125000,
    orders: 2500,
    avgOrderValue: 50,
    growth: 12.5
  },
  {
    id: '2',
    name: 'Restaurant B',
    revenue: 98000,
    orders: 1800,
    avgOrderValue: 54.44,
    growth: -2.3
  },
  {
    id: '3',
    name: 'Restaurant C',
    revenue: 156000,
    orders: 3100,
    avgOrderValue: 50.32,
    growth: 8.7
  }
])

const sortedRestaurants = computed(() => {
  return [...restaurants.value].sort((a, b) => {
    switch (sortBy.value) {
      case 'revenue':
        return b.revenue - a.revenue
      case 'orders':
        return b.orders - a.orders
      case 'rating':
        return b.avgOrderValue - a.avgOrderValue
      case 'growth':
        return b.growth - a.growth
      default:
        return 0
    }
  })
})

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value)
}

const exportAnalytics = () => {
  // Implement export functionality
  console.log('Exporting analytics...')
}

const viewDetails = (restaurant: Restaurant) => {
  // Implement view details functionality
  console.log('Viewing details for:', restaurant.name)
}
</script> 