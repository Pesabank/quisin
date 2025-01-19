<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()
const apiClient = useApiClient()

// Dashboard statistics for Kitchen Staff
const stats = ref({
  activeOrders: 0,
  completedOrders: 0,
  averagePreparationTime: 0
})

const activeOrderColumns = [
  { key: 'id', label: 'Order #', sortable: true },
  { key: 'tableNumber', label: 'Table', sortable: true },
  { key: 'items', label: 'Items', sortable: false }
]

const activeOrders = ref<any[]>([])

// Fetch dashboard data
async function fetchDashboardData() {
  try {
    const { data, error } = await apiClient.get('/kitchen/dashboard')
    
    if (error.value) {
      throw new Error('Failed to fetch dashboard data')
    }

    stats.value = data.value?.stats || stats.value
    activeOrders.value = data.value?.activeOrders || []
  } catch (error) {
    console.error('Dashboard data fetch error:', error)
    useToast().error('Failed to load dashboard data')
  }
}

// Order actions
const orderActions = [
  {
    label: 'Complete',
    handler: async (order: any) => {
      try {
        await apiClient.post(`/kitchen/orders/${order.id}/complete`)
        await fetchDashboardData()
        useToast().success('Order completed')
      } catch (error) {
        useToast().error('Failed to complete order')
      }
    }
  },
  {
    label: 'View Details',
    handler: (order: any) => {
      navigateTo(`/kitchen/orders/${order.id}`)
    },
    class: 'bg-blue-500 hover:bg-blue-600'
  }
]

onMounted(fetchDashboardData)
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="container mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 mb-6">
        Kitchen Dashboard
      </h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <StatCard 
          title="Active Orders" 
          :value="stats.activeOrders" 
          icon="spinner"
        />
        <StatCard 
          title="Completed Orders" 
          :value="stats.completedOrders" 
          icon="check-circle"
          color="#10B981"
        />
        <StatCard 
          title="Avg. Prep Time" 
          :value="`${stats.averagePreparationTime} min`" 
          icon="clock"
          color="#3B82F6"
        />
      </div>

      <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-semibold text-[#FF6B00] mb-4">
          Active Order Queue
        </h2>
        <DataTable 
          :columns="activeOrderColumns"
          :data="activeOrders"
          :actions="orderActions"
          empty-message="No active orders"
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
