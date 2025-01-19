<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Waiter Dashboard</h1>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-500">Active Tables: {{ activeTables }}/{{ tables.length }}</span>
            <button
              @click="refreshData"
              class="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Refresh
            </button>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <TableGrid
          :tables="tables"
          @seat-guests="openOrderForm"
          @view-order="viewOrder"
          @manage-table="manageTable"
        />
      </div>
    </main>

    <!-- Order Form Modal -->
    <TransitionRoot appear :show="isOrderFormOpen" as="template">
      <Dialog as="div" @close="closeOrderForm" class="relative z-10">
        <TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 z-10 overflow-y-auto">
          <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            >
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-3xl sm:p-6">
                <OrderForm
                  v-if="selectedTable"
                  :table-number="selectedTable.number"
                  :menu-items="menuItems"
                  :categories="categories"
                  @submit="submitOrder"
                  @cancel="closeOrderForm"
                />
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Dialog, DialogPanel, TransitionChild, TransitionRoot } from '@headlessui/vue'
import TableGrid from '@/components/waiter/TableGrid.vue'
import OrderForm from '@/components/waiter/OrderForm.vue'

// Mock data - replace with actual API calls
const tables = ref([
  { id: '1', number: 1, capacity: 4, status: 'available' },
  { id: '2', number: 2, capacity: 2, status: 'occupied', currentOrder: { id: 'O1', items: 3, startTime: '2024-12-14T00:15:00' } },
  { id: '3', number: 3, capacity: 6, status: 'reserved', reservation: { customerName: 'John Doe', time: '2024-12-14T01:00:00' } },
])

const menuItems = ref([
  { id: 'M1', name: 'Margherita Pizza', description: 'Fresh tomatoes, mozzarella, basil', price: 12.99, category: 'Pizza' },
  { id: 'M2', name: 'Caesar Salad', description: 'Romaine lettuce, croutons, parmesan', price: 8.99, category: 'Salads' },
])

const categories = computed(() => {
  return [...new Set(menuItems.value.map(item => item.category))]
})

const activeTables = computed(() => {
  return tables.value.filter(table => table.status === 'occupied').length
})

const isOrderFormOpen = ref(false)
const selectedTable = ref(null)

const openOrderForm = (table) => {
  selectedTable.value = table
  isOrderFormOpen.value = true
}

const closeOrderForm = () => {
  selectedTable.value = null
  isOrderFormOpen.value = false
}

const submitOrder = async (orderData) => {
  try {
    console.log('Submitting order:', orderData)
    
    if (selectedTable.value) {
      const table = tables.value.find(t => t.id === selectedTable.value.id)
      if (table) {
        table.status = 'occupied'
        table.currentOrder = {
          id: `O${Date.now()}`,
          items: orderData.items.length,
          startTime: new Date().toISOString()
        }
      }
    }
    
    closeOrderForm()
  } catch (error) {
    console.error('Error submitting order:', error)
  }
}

const viewOrder = (table) => {
  console.log('Viewing order for table:', table)
}

const manageTable = (table) => {
  console.log('Managing table:', table)
}

const refreshData = async () => {
  try {
    console.log('Refreshing data...')
  } catch (error) {
    console.error('Error refreshing data:', error)
  }
}

onMounted(() => {
  refreshData()
})
</script>
