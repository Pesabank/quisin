<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()
const apiClient = useApiClient()

// Dashboard statistics
const stats = ref({
  totalStaff: 0,
  activeStaff: 0,
  totalOrders: 0,
  pendingOrders: 0
})

const staffColumns = [
  { key: 'name', label: 'Staff Name', sortable: true },
  { key: 'role', label: 'Role', sortable: true },
  { key: 'status', label: 'Status', sortable: true }
]

const staffData = ref<any[]>([])

// Fetch dashboard data
async function fetchDashboardData() {
  try {
    const { data, error } = await apiClient.get('/restaurant/dashboard')
    
    if (error.value) {
      throw new Error('Failed to fetch dashboard data')
    }

    stats.value = data.value?.stats || stats.value
    staffData.value = data.value?.staff || []
  } catch (error) {
    console.error('Dashboard data fetch error:', error)
    useToast().error('Failed to load dashboard data')
  }
}

// Staff actions
const staffActions = [
  {
    label: 'View Profile',
    handler: (staff: any) => {
      navigateTo(`/admin/staff/${staff.id}`)
    }
  },
  {
    label: 'Toggle Status',
    handler: async (staff: any) => {
      try {
        await apiClient.post(`/admin/staff/${staff.id}/toggle-status`)
        await fetchDashboardData()
        useToast().success(`Staff status updated`)
      } catch (error) {
        useToast().error('Failed to update staff status')
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
        Restaurant Admin Dashboard
      </h1>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <StatCard 
          title="Total Staff" 
          :value="stats.totalStaff" 
          icon="users"
        />
        <StatCard 
          title="Active Staff" 
          :value="stats.activeStaff" 
          icon="user-check"
          color="#10B981"
        />
        <StatCard 
          title="Total Orders" 
          :value="stats.totalOrders" 
          icon="shopping-cart"
          color="#3B82F6"
        />
        <StatCard 
          title="Pending Orders" 
          :value="stats.pendingOrders" 
          icon="clock"
          color="#F59E0B"
        />
      </div>

      <div class="bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-semibold text-[#FF6B00] mb-4">
          Staff Management
        </h2>
        <DataTable 
          :columns="staffColumns"
          :data="staffData"
          :actions="staffActions"
          empty-message="No staff members found"
        />
      </div>

      <div class="mt-8 space-x-4">
        <button 
          @click="navigateTo('/admin/staff')"
          class="bg-[#FF6B00] text-white px-4 py-2 rounded hover:bg-orange-700 transition"
        >
          Manage Staff
        </button>
        <button 
          @click="navigateTo('/admin/orders')"
          class="bg-[#FF6B00] text-white px-4 py-2 rounded hover:bg-orange-700 transition"
        >
          View Orders
        </button>
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
