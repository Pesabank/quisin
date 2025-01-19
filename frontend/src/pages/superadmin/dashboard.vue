<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()
const apiClient = useApiClient()

// Dashboard statistics
const stats = ref({
  totalRestaurants: 0,
  activeRestaurants: 0,
  totalUsers: 0
})

const restaurantColumns = [
  { key: 'name', label: 'Restaurant Name', sortable: true },
  { key: 'status', label: 'Status', sortable: true },
  { key: 'createdAt', label: 'Created', sortable: true }
]

const restaurantData = ref<any[]>([])

// Fetch dashboard data
async function fetchDashboardData() {
  try {
    const { data, error } = await apiClient.get('/superadmin/dashboard')
    
    if (error.value) {
      throw new Error('Failed to fetch dashboard data')
    }

    stats.value = data.value?.stats || stats.value
    restaurantData.value = data.value?.restaurants || []
  } catch (error) {
    console.error('Dashboard data fetch error:', error)
    useToast().error('Failed to load dashboard data')
  }
}

// Restaurant actions
const restaurantActions = [
  {
    label: 'View Details',
    handler: (restaurant: any) => {
      navigateTo(`/superadmin/restaurants/${restaurant.id}`)
    }
  },
  {
    label: 'Toggle Status',
    handler: async (restaurant: any) => {
      try {
        await apiClient.post(`/superadmin/restaurants/${restaurant.id}/toggle-status`)
        await fetchDashboardData()
        useToast().success(`Restaurant status updated`)
      } catch (error) {
        useToast().error('Failed to update restaurant status')
      }
    },
    class: 'bg-yellow-500 hover:bg-yellow-600'
  }
]

onMounted(fetchDashboardData)
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="container mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 mb-6">
        Superadmin Dashboard
      </h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <StatCard 
          title="Total Restaurants" 
          :value="stats.totalRestaurants" 
          icon="store"
        />
        <StatCard 
          title="Active Restaurants" 
          :value="stats.activeRestaurants" 
          icon="check-circle"
          color="#10B981"
        />
        <StatCard 
          title="Total Users" 
          :value="stats.totalUsers" 
          icon="users"
          color="#3B82F6"
        />
      </div>

      <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-semibold text-[#FF6B00] mb-4">
          Restaurant Management
        </h2>
        <DataTable 
          :columns="restaurantColumns"
          :data="restaurantData"
          :actions="restaurantActions"
          empty-message="No restaurants found"
        />
      </div>

      <div class="mt-8">
        <button 
          @click="authStore.logout"
          class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition"
        >
          Logout
        </button>
      </div>
    </div>
  </div>
</template>
