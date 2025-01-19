<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header with Filters -->
      <div class="flex justify-between items-center mb-8">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Analytics Dashboard</h1>
          <p class="mt-1 text-sm text-gray-600">Track performance across your restaurant chain</p>
        </div>
        <div class="flex items-center gap-4">
          <select
            v-model="selectedRestaurant"
            class="block w-48 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-sm"
          >
            <option value="all">All Restaurants</option>
            <option value="downtown">Downtown Branch</option>
            <option value="uptown">Uptown Branch</option>
            <option value="waterfront">Waterfront Branch</option>
          </select>
          <select
            v-model="timeRange"
            class="block w-40 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-sm"
          >
            <option value="today">Today</option>
            <option value="week">This Week</option>
            <option value="month">This Month</option>
            <option value="year">This Year</option>
          </select>
        </div>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4 mb-8">
        <!-- Total Revenue -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Total Revenue</p>
              <div class="flex items-center mt-1">
                <p class="text-2xl font-semibold text-gray-900">KSH 125,000</p>
                <span class="ml-2 flex items-center text-sm font-medium text-green-600">
                  <ArrowTrendingUpIcon class="h-4 w-4 mr-1" />
                  +12.5%
                </span>
              </div>
            </div>
            <div class="bg-blue-100 p-3 rounded-lg">
              <CurrencyDollarIcon class="h-6 w-6 text-blue-600" />
            </div>
          </div>
        </div>

        <!-- Total Orders -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Total Orders</p>
              <div class="flex items-center mt-1">
                <p class="text-2xl font-semibold text-gray-900">1,250</p>
                <span class="ml-2 flex items-center text-sm font-medium text-green-600">
                  <ArrowTrendingUpIcon class="h-4 w-4 mr-1" />
                  +8.3%
                </span>
              </div>
            </div>
            <div class="bg-orange-100 p-3 rounded-lg">
              <ShoppingBagIcon class="h-6 w-6 text-orange-600" />
            </div>
          </div>
        </div>

        <!-- Average Order Value -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Average Order</p>
              <div class="flex items-center mt-1">
                <p class="text-2xl font-semibold text-gray-900">KSH 42</p>
                <span class="ml-2 flex items-center text-sm font-medium text-red-600">
                  <ArrowTrendingDownIcon class="h-4 w-4 mr-1" />
                  -2.1%
                </span>
              </div>
            </div>
            <div class="bg-yellow-100 p-3 rounded-lg">
              <CalculatorIcon class="h-6 w-6 text-yellow-600" />
            </div>
          </div>
        </div>

        <!-- Customer Satisfaction -->
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Customer Satisfaction</p>
              <div class="flex items-center mt-1">
                <p class="text-2xl font-semibold text-gray-900">4.8/5</p>
                <span class="ml-2 flex items-center text-sm font-medium text-green-600">
                  <ArrowTrendingUpIcon class="h-4 w-4 mr-1" />
                  +5.2%
                </span>
              </div>
            </div>
            <div class="bg-green-100 p-3 rounded-lg">
              <StarIcon class="h-6 w-6 text-green-600" />
            </div>
          </div>
        </div>
      </div>

      <!-- Charts Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
        <!-- Revenue Overview -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Revenue Overview</h3>
          <canvas ref="revenueChart" class="w-full h-64"></canvas>
        </div>

        <!-- Order Trends -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Order Trends</h3>
          <canvas ref="orderTrendsChart" class="w-full h-64"></canvas>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Most Popular Items -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Most Popular Items</h3>
          <canvas ref="popularItemsChart" class="w-full h-64"></canvas>
        </div>

        <!-- Top Performing Locations -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Top Performing Locations</h3>
          <div class="space-y-4">
            <div v-for="location in topLocations" :key="location.name" class="flex items-center justify-between">
              <div>
                <p class="font-medium text-gray-900">{{ location.name }}</p>
                <p class="text-sm text-gray-500">KSH {{ location.revenue.toLocaleString() }}</p>
              </div>
              <div class="flex items-center space-x-4">
                <span class="text-sm text-gray-500">{{ location.orders }} orders</span>
                <span :class="[
                  'text-sm font-medium px-2 py-1 rounded-full',
                  location.growth >= 0 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                ]">
                  {{ location.growth >= 0 ? '+' : '' }}{{ location.growth }}%
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  CurrencyDollarIcon,
  ShoppingBagIcon,
  CalculatorIcon,
  StarIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon
} from '@heroicons/vue/24/outline'
import Chart from 'chart.js/auto'

definePageMeta({
  layout: 'admin'
})

const selectedRestaurant = ref('all')
const timeRange = ref('month')

const revenueChart = ref(null)
const orderTrendsChart = ref(null)
const popularItemsChart = ref(null)

const topLocations = ref([
  {
    name: 'Downtown Branch',
    revenue: 52000,
    orders: 520,
    growth: 16.2
  },
  {
    name: 'Uptown Branch',
    revenue: 48000,
    orders: 485,
    growth: 12.9
  },
  {
    name: 'Waterfront Branch',
    revenue: 25000,
    orders: 245,
    growth: -2.5
  }
])

onMounted(() => {
  // Revenue Chart
  new Chart(revenueChart.value, {
    type: 'line',
    data: {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
      datasets: [{
        label: 'Revenue',
        data: [30000, 35000, 32000, 38000, 42000, 48000],
        borderColor: '#FF6B00',
        backgroundColor: 'rgba(255, 107, 0, 0.1)',
        fill: true,
        tension: 0.4
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => `KSH ${value.toLocaleString()}`
          }
        }
      }
    }
  })

  // Order Trends Chart
  new Chart(orderTrendsChart.value, {
    type: 'bar',
    data: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      datasets: [{
        label: 'Orders',
        data: [65, 75, 78, 72, 85, 82, 75],
        backgroundColor: '#FF6B00'
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  })

  // Popular Items Chart
  new Chart(popularItemsChart.value, {
    type: 'pie',
    data: {
      labels: ['Burgers', 'Pizza', 'Salads', 'Desserts', 'Beverages'],
      datasets: [{
        data: [30, 25, 15, 20, 10],
        backgroundColor: [
          '#FF6B00',
          '#FF8533',
          '#FFA366',
          '#FFC199',
          '#FFD9CC'
        ]
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'right'
        }
      }
    }
  })
})
</script> 