<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Table Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            View and manage restaurant tables and seating arrangements
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showAddTableModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Table
          </button>
        </div>
      </div>

      <!-- Table Layout View -->
      <div class="mt-8 bg-white rounded-lg shadow p-6">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-lg font-medium text-gray-900">Restaurant Floor Plan</h2>
          <div class="flex items-center space-x-4">
            <!-- Legend -->
            <div class="flex items-center">
              <span class="w-3 h-3 rounded-full bg-green-500 mr-2"></span>
              <span class="text-sm text-gray-600">Available</span>
            </div>
            <div class="flex items-center">
              <span class="w-3 h-3 rounded-full bg-red-500 mr-2"></span>
              <span class="text-sm text-gray-600">Occupied</span>
            </div>
            <div class="flex items-center">
              <span class="w-3 h-3 rounded-full bg-yellow-500 mr-2"></span>
              <span class="text-sm text-gray-600">Reserved</span>
            </div>
          </div>
        </div>

        <!-- Grid Layout -->
        <div class="grid grid-cols-6 gap-4 p-4 bg-gray-50 rounded-lg min-h-[600px] relative">
          <!-- Tables -->
          <div
            v-for="table in tables"
            :key="table.id"
            @click="openTableDetails(table)"
            :class="[
              'cursor-pointer transform transition-all duration-200 hover:scale-105',
              'flex flex-col items-center justify-center p-4 rounded-lg shadow-md',
              table.status === 'available' ? 'bg-green-100 hover:bg-green-200' :
              table.status === 'occupied' ? 'bg-red-100 hover:bg-red-200' :
              'bg-yellow-100 hover:bg-yellow-200'
            ]"
            :style="{
              gridColumn: `span ${table.size <= 4 ? 1 : 2}`,
              gridRow: `span ${table.size <= 4 ? 1 : 2}`
            }"
          >
            <div class="relative w-full h-full">
              <!-- Table Shape -->
              <div
                :class="[
                  'absolute inset-0 border-2 rounded-lg flex items-center justify-center',
                  table.status === 'available' ? 'border-green-500' :
                  table.status === 'occupied' ? 'border-red-500' :
                  'border-yellow-500'
                ]"
              >
                <div class="text-center">
                  <p class="font-medium text-gray-900">Table {{ table.number }}</p>
                  <p class="text-sm text-gray-600">{{ table.size }} Seats</p>
                </div>
              </div>
              
              <!-- Seats Visualization -->
              <div class="absolute inset-0 flex items-center justify-center">
                <div
                  v-for="seat in table.size"
                  :key="seat"
                  :style="{
                    transform: `rotate(${(360 / table.size) * seat}deg) translateY(-${table.size <= 4 ? '2' : '3'}rem)`
                  }"
                  class="absolute w-3 h-3 rounded-full bg-gray-400"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Table Details Modal -->
    <TransitionRoot appear :show="showTableDetailsModal" as="template">
      <Dialog as="div" @close="showTableDetailsModal = false" class="relative z-10">
        <TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black bg-opacity-25" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4 text-center">
            <TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
                <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                  Table {{ selectedTable?.number }} Details
                </DialogTitle>

                <div class="mt-4 space-y-4">
                  <div>
                    <p class="text-sm font-medium text-gray-500">Status</p>
                    <p :class="[
                      'mt-1 text-sm font-medium',
                      selectedTable?.status === 'available' ? 'text-green-600' :
                      selectedTable?.status === 'occupied' ? 'text-red-600' :
                      'text-yellow-600'
                    ]">
                      {{ selectedTable?.status.charAt(0).toUpperCase() + selectedTable?.status.slice(1) }}
                    </p>
                  </div>

                  <div>
                    <p class="text-sm font-medium text-gray-500">Seating Capacity</p>
                    <p class="mt-1 text-sm text-gray-900">{{ selectedTable?.size }} People</p>
                  </div>

                  <div v-if="selectedTable?.status === 'occupied' || selectedTable?.status === 'reserved'">
                    <p class="text-sm font-medium text-gray-500">Customer Details</p>
                    <div class="mt-1 text-sm text-gray-900">
                      <p>{{ selectedTable?.customerName }}</p>
                      <p>{{ selectedTable?.reservationTime }}</p>
                    </div>
                  </div>

                  <div class="mt-6 flex justify-end space-x-3">
                    <button
                      v-if="selectedTable?.status === 'available'"
                      @click="markTableAsReserved"
                      class="px-4 py-2 text-sm font-medium text-white bg-[#FF6B00] rounded-md hover:bg-[#e66000]"
                    >
                      Mark as Reserved
                    </button>
                    <button
                      v-if="selectedTable?.status === 'reserved'"
                      @click="markTableAsOccupied"
                      class="px-4 py-2 text-sm font-medium text-white bg-[#FF6B00] rounded-md hover:bg-[#e66000]"
                    >
                      Mark as Occupied
                    </button>
                    <button
                      v-if="selectedTable?.status !== 'available'"
                      @click="markTableAsAvailable"
                      class="px-4 py-2 text-sm font-medium text-green-600 bg-green-100 rounded-md hover:bg-green-200"
                    >
                      Mark as Available
                    </button>
                    <button
                      @click="showTableDetailsModal = false"
                      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                    >
                      Close
                    </button>
                  </div>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Add Table Modal -->
    <TransitionRoot appear :show="showAddTableModal" as="template">
      <Dialog as="div" @close="showAddTableModal = false" class="relative z-10">
        <TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black bg-opacity-25" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4 text-center">
            <TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
                <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                  Add New Table
                </DialogTitle>

                <form @submit.prevent="addTable" class="mt-4">
                  <div class="space-y-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">Table Number</label>
                      <input
                        type="number"
                        v-model="newTable.number"
                        min="1"
                        class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        required
                      />
                    </div>

                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">Seating Capacity</label>
                      <select
                        v-model="newTable.size"
                        class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        required
                      >
                        <option value="2">2 Seats</option>
                        <option value="4">4 Seats</option>
                        <option value="6">6 Seats</option>
                        <option value="8">8 Seats</option>
                      </select>
                    </div>
                  </div>

                  <div class="mt-6 flex justify-end space-x-3">
                    <button
                      type="button"
                      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                      @click="showAddTableModal = false"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      class="px-4 py-2 text-sm font-medium text-white bg-[#FF6B00] rounded-md hover:bg-[#e66000]"
                    >
                      Add Table
                    </button>
                  </div>
                </form>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { PlusIcon } from '@heroicons/vue/24/outline'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'

definePageMeta({
  layout: 'admin'
})

interface Table {
  id: string
  number: number
  size: number
  status: 'available' | 'occupied' | 'reserved'
  customerName?: string
  reservationTime?: string
}

// State
const showAddTableModal = ref(false)
const showTableDetailsModal = ref(false)
const selectedTable = ref<Table | null>(null)
const newTable = ref({
  number: 1,
  size: 4
})

// Mock data
const tables = ref<Table[]>([
  {
    id: '1',
    number: 1,
    size: 4,
    status: 'available'
  },
  {
    id: '2',
    number: 2,
    size: 2,
    status: 'occupied',
    customerName: 'John Doe',
    reservationTime: '7:00 PM'
  },
  {
    id: '3',
    number: 3,
    size: 6,
    status: 'reserved',
    customerName: 'Jane Smith',
    reservationTime: '8:30 PM'
  },
  {
    id: '4',
    number: 4,
    size: 4,
    status: 'available'
  },
  {
    id: '5',
    number: 5,
    size: 8,
    status: 'available'
  },
  {
    id: '6',
    number: 6,
    size: 2,
    status: 'occupied',
    customerName: 'Mike Johnson',
    reservationTime: '6:45 PM'
  }
])

// Methods
const openTableDetails = (table: Table) => {
  selectedTable.value = table
  showTableDetailsModal.value = true
}

const markTableAsReserved = () => {
  if (selectedTable.value) {
    selectedTable.value.status = 'reserved'
    selectedTable.value.customerName = 'New Reservation'
    selectedTable.value.reservationTime = '7:00 PM'
    showTableDetailsModal.value = false
    // TODO: API call to update table status
  }
}

const markTableAsOccupied = () => {
  if (selectedTable.value) {
    selectedTable.value.status = 'occupied'
    showTableDetailsModal.value = false
    // TODO: API call to update table status
  }
}

const markTableAsAvailable = () => {
  if (selectedTable.value) {
    selectedTable.value.status = 'available'
    selectedTable.value.customerName = undefined
    selectedTable.value.reservationTime = undefined
    showTableDetailsModal.value = false
    // TODO: API call to update table status
  }
}

const addTable = () => {
  const table: Table = {
    id: (tables.value.length + 1).toString(),
    number: newTable.value.number,
    size: newTable.value.size,
    status: 'available'
  }
  tables.value.push(table)
  showAddTableModal.value = false
  newTable.value = {
    number: tables.value.length + 1,
    size: 4
  }
  // TODO: API call to add table
}
</script> 