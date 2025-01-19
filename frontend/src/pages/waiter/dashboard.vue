<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()
const apiClient = useApiClient()

// Dashboard data for Waiter
const stats = ref({
  totalTables: 0,
  occupiedTables: 0,
  activeOrders: 0
})

const tableColumns = [
  { key: 'number', label: 'Table #', sortable: true },
  { key: 'status', label: 'Status', sortable: true },
  { key: 'currentOrder', label: 'Current Order', sortable: false }
]

const tables = ref<any[]>([])

// Fetch dashboard data
async function fetchDashboardData() {
  try {
    const { data, error } = await apiClient.get('/waiter/dashboard')
    
    if (error.value) {
      throw new Error('Failed to fetch dashboard data')
    }

    stats.value = data.value?.stats || stats.value
    tables.value = data.value?.tables || []
  } catch (error) {
    console.error('Dashboard data fetch error:', error)
    useToast().error('Failed to load dashboard data')
  }
}

// Table actions
const tableActions = [
  {
    label: 'Create Order',
    handler: (table: any) => {
      navigateTo(`/waiter/order/create?tableId=${table.id}`)
    }
  },
  {
    label: 'Toggle Status',
    handler: async (table: any) => {
      try {
        await apiClient.post(`/waiter/tables/${table.id}/toggle-status`)
        await fetchDashboardData()
        useToast().success('Table status updated')
      } catch (error) {
        useToast().error('Failed to update table status')
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
        Waiter Dashboard
      </h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <StatCard 
          title="Total Tables" 
          :value="stats.totalTables" 
          icon="table"
        />
        <StatCard 
          title="Occupied Tables" 
          :value="stats.occupiedTables" 
          icon="chair"
          color="#10B981"
        />
        <StatCard 
          title="Active Orders" 
          :value="stats.activeOrders" 
          icon="shopping-cart"
          color="#3B82F6"
        />
      </div>

      <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-semibold text-[#FF6B00] mb-4">
          Table Management
        </h2>
        <DataTable 
          :columns="tableColumns"
          :data="tables"
          :actions="tableActions"
          empty-message="No tables available"
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
