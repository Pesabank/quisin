<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()
const apiClient = useApiClient()

// Customer dashboard data
const stats = ref({
  loyaltyPoints: 0,
  totalOrders: 0,
  totalSpent: 0
})

const orderColumns = [
  { key: 'id', label: 'Order #', sortable: true },
  { key: 'date', label: 'Date', sortable: true },
  { key: 'total', label: 'Total', sortable: true }
]

const orderHistory = ref<any[]>([])

// Fetch dashboard data
async function fetchDashboardData() {
  try {
    const { data, error } = await apiClient.get('/customer/dashboard')
    
    if (error.value) {
      throw new Error('Failed to fetch dashboard data')
    }

    stats.value = data.value?.stats || stats.value
    orderHistory.value = data.value?.orderHistory || []
  } catch (error) {
    console.error('Dashboard data fetch error:', error)
    useToast().error('Failed to load dashboard data')
  }
}

// Order actions
const orderActions = [
  {
    label: 'View Details',
    handler: (order: any) => {
      navigateTo(`/customer/orders/${order.id}`)
    }
  },
  {
    label: 'Reorder',
    handler: async (order: any) => {
      try {
        await navigateTo(`/customer/order/reorder/${order.id}`)
      } catch (error) {
        useToast().error('Failed to create reorder')
      }
    },
    class: 'bg-[#FF6B00] hover:bg-orange-700'
  }
]

onMounted(fetchDashboardData)
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="container mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 mb-6">
        Customer Dashboard
      </h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <StatCard 
          title="Loyalty Points" 
          :value="stats.loyaltyPoints" 
          icon="star"
        />
        <StatCard 
          title="Total Orders" 
          :value="stats.totalOrders" 
          icon="shopping-cart"
          color="#10B981"
        />
        <StatCard 
          title="Total Spent" 
          :value="`$${stats.totalSpent}`" 
          icon="dollar-sign"
          color="#3B82F6"
        />
      </div>

      <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-semibold text-[#FF6B00] mb-4">
          Order History
        </h2>
        <DataTable 
          :columns="orderColumns"
          :data="orderHistory"
          :actions="orderActions"
          empty-message="No order history"
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
