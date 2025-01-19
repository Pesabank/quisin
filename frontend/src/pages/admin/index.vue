<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Restaurant Admin Dashboard</h1>
          <div class="flex items-center space-x-4">
            <button
              @click="refreshData"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
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

      <!-- Quick Actions -->
      <div class="bg-white shadow rounded-lg mb-8">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg font-medium leading-6 text-gray-900">Quick Actions</h3>
          <div class="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-3">
            <button
              v-for="action in quickActions"
              :key="action.name"
              @click="action.handler"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              {{ action.name }}
            </button>
          </div>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <h3 class="text-lg font-medium leading-6 text-gray-900 mb-4">Recent Activity</h3>
          <div class="flow-root">
            <ul role="list" class="-mb-8">
              <li v-for="(activity, index) in recentActivity" :key="activity.id">
                <div class="relative pb-8">
                  <span
                    v-if="index !== recentActivity.length - 1"
                    class="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-200"
                    aria-hidden="true"
                  />
                  <div class="relative flex space-x-3">
                    <div>
                      <span
                        :class="[
                          activity.type === 'warning' ? 'bg-yellow-500' : 'bg-blue-500',
                          'h-8 w-8 rounded-full flex items-center justify-center ring-8 ring-white'
                        ]"
                      >
                        <component
                          :is="activity.icon"
                          class="h-5 w-5 text-white"
                          aria-hidden="true"
                        />
                      </span>
                    </div>
                    <div class="min-w-0 flex-1 pt-1.5 flex justify-between space-x-4">
                      <div>
                        <p class="text-sm text-gray-500">
                          {{ activity.content }}
                          <a :href="activity.href" class="font-medium text-gray-900">
                            {{ activity.target }}
                          </a>
                        </p>
                      </div>
                      <div class="text-right text-sm whitespace-nowrap text-gray-500">
                        <time :datetime="activity.datetime">{{ activity.date }}</time>
                      </div>
                    </div>
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
    name: 'Active Orders',
    value: '23',
    change: { value: '+4.75%', type: 'increase' },
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
      `
    })
  },
  {
    name: 'Staff Online',
    value: '15',
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
      `
    })
  },
  {
    name: 'Customer Satisfaction',
    value: '98%',
    change: { value: '+2.5%', type: 'increase' },
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      `
    })
  }
])

const quickActions = [
  {
    name: 'View Orders',
    handler: () => router.push('/admin/orders')
  },
  {
    name: 'Manage Staff',
    handler: () => router.push('/admin/staff')
  },
  {
    name: 'View Reports',
    handler: () => router.push('/admin/reports')
  }
]

const recentActivity = ref([
  {
    id: 1,
    content: 'New order received from',
    target: 'Table 12',
    href: '#',
    date: '20 minutes ago',
    datetime: '2024-01-07T13:00Z',
    type: 'success',
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      `
    })
  },
  {
    id: 2,
    content: 'Kitchen reported delay in',
    target: 'Order #1234',
    href: '#',
    date: '1 hour ago',
    datetime: '2024-01-07T12:00Z',
    type: 'warning',
    icon: defineComponent({
      template: `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      `
    })
  }
])

const refreshData = async () => {
  try {
    // TODO: Implement API calls to refresh dashboard data
    console.log('Refreshing dashboard data...')
  } catch (error) {
    console.error('Error refreshing data:', error)
  }
}

onMounted(() => {
  refreshData()
})
</script>
