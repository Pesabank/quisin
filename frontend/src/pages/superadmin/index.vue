<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Super Admin Dashboard</h1>
          <div class="flex items-center space-x-4">
            <button
              @click="refreshData"
              class="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Refresh Data
            </button>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Restaurant Management Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="p-5">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">Total Restaurants</dt>
                    <dd class="flex items-baseline">
                      <div class="text-2xl font-semibold text-gray-900">{{ stats.totalRestaurants }}</div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-5 py-3">
              <div class="text-sm">
                <a href="#" class="font-medium text-blue-600 hover:text-blue-900" @click.prevent="navigateToRestaurants">
                  Manage restaurants →
                </a>
              </div>
            </div>
          </div>

          <!-- Users Management Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="p-5">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">Total Users</dt>
                    <dd class="flex items-baseline">
                      <div class="text-2xl font-semibold text-gray-900">{{ stats.totalUsers }}</div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-5 py-3">
              <div class="text-sm">
                <a href="#" class="font-medium text-blue-600 hover:text-blue-900" @click.prevent="navigateToUsers">
                  Manage users →
                </a>
              </div>
            </div>
          </div>

          <!-- System Health Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="p-5">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">System Status</dt>
                    <dd class="flex items-baseline">
                      <div class="text-2xl font-semibold text-gray-900">{{ stats.systemHealth }}</div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-5 py-3">
              <div class="text-sm">
                <a href="#" class="font-medium text-blue-600 hover:text-blue-900" @click.prevent="navigateToSystemHealth">
                  View system health →
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Activity -->
        <div class="mt-8">
          <h2 class="text-lg font-medium text-gray-900">Recent Activity</h2>
          <div class="mt-4 bg-white shadow overflow-hidden sm:rounded-lg">
            <ul role="list" class="divide-y divide-gray-200">
              <li v-for="activity in recentActivities" :key="activity.id" class="px-4 py-4 sm:px-6">
                <div class="flex items-center justify-between">
                  <p class="text-sm font-medium text-blue-600 truncate">{{ activity.action }}</p>
                  <div class="ml-2 flex-shrink-0 flex">
                    <p class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                       :class="activity.type === 'warning' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'">
                      {{ activity.type }}
                    </p>
                  </div>
                </div>
                <div class="mt-2 sm:flex sm:justify-between">
                  <div class="sm:flex">
                    <p class="flex items-center text-sm text-gray-500">
                      {{ activity.description }}
                    </p>
                  </div>
                  <div class="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                    <p>{{ activity.timestamp }}</p>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

interface Stats {
  totalRestaurants: number
  totalUsers: number
  systemHealth: string
}

interface Activity {
  id: string
  action: string
  description: string
  type: 'warning' | 'success'
  timestamp: string
}

const stats = ref<Stats>({
  totalRestaurants: 0,
  totalUsers: 0,
  systemHealth: 'Healthy'
})

const recentActivities = ref<Activity[]>([
  {
    id: '1',
    action: 'New Restaurant Added',
    description: 'Restaurant "La Piazza" was added to the system',
    type: 'success',
    timestamp: '30 minutes ago'
  },
  {
    id: '2',
    action: 'System Alert',
    description: 'High CPU usage detected on order service',
    type: 'warning',
    timestamp: '2 hours ago'
  }
])

const refreshData = async () => {
  try {
    // TODO: Implement API calls to fetch latest data
    console.log('Refreshing dashboard data...')
  } catch (error) {
    console.error('Error refreshing data:', error)
  }
}

const navigateToRestaurants = () => {
  router.push('/superadmin/restaurants')
}

const navigateToUsers = () => {
  router.push('/superadmin/users')
}

const navigateToSystemHealth = () => {
  router.push('/superadmin/system-health')
}

onMounted(() => {
  refreshData()
})
</script>
