<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-semibold text-gray-900">Table Management</h1>
        <button
          @click="showAddTableModal = true"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
        >
          <PlusIcon class="h-5 w-5 mr-1" />
          Add Table
        </button>
      </div>

      <!-- Table Grid -->
      <div class="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="table in tables" :key="table.id" class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-6">
            <div class="flex justify-between items-start">
              <div>
                <h3 class="text-lg font-medium text-gray-900">{{ table.name }}</h3>
                <p class="mt-1 text-sm text-gray-500">{{ table.seats }} seats</p>
                <p v-if="table.location" class="mt-1 text-sm text-gray-500">{{ table.location }}</p>
              </div>
              <span
                :class="[
                  'px-2 py-1 text-xs font-medium rounded-full',
                  table.status === 'available' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                ]"
              >
                {{ table.status }}
              </span>
            </div>
            <div class="mt-4 text-sm text-gray-500">
              <p class="font-mono">ID: {{ table.id }}</p>
            </div>
            <div class="mt-4 flex justify-between items-center">
              <button
                @click="downloadQR(table.id)"
                class="inline-flex items-center text-sm text-[#FF6B00] hover:text-[#e66000]"
              >
                <QrCodeIcon class="h-5 w-5 mr-1" />
                Download QR
              </button>
              <button
                @click="deleteTable(table.id)"
                class="inline-flex items-center text-sm text-red-600 hover:text-red-700"
              >
                <TrashIcon class="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Add Table Modal -->
      <TransitionRoot appear :show="showAddTableModal" as="template">
        <Dialog as="div" class="relative z-10" @close="showAddTableModal = false">
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
                <DialogPanel class="w-full max-w-lg transform overflow-hidden rounded-lg bg-white p-8 text-left align-middle shadow-xl transition-all">
                  <div class="flex justify-between items-center border-b border-gray-200 pb-4">
                    <DialogTitle as="h3" class="text-xl font-semibold leading-6 text-gray-900">
                      Add New Table
                    </DialogTitle>
                    <button @click="showAddTableModal = false" class="text-gray-400 hover:text-gray-500">
                      <XMarkIcon class="h-6 w-6" />
                    </button>
                  </div>

                  <div class="mt-6 space-y-6">
                    <div>
                      <label for="tableNumber" class="block text-base font-medium text-gray-900 mb-2">Table Number</label>
                      <input
                        type="text"
                        id="tableNumber"
                        v-model="newTable.number"
                        class="block w-full px-4 py-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        placeholder="e.g., A1"
                      />
                    </div>

                    <div>
                      <label for="seats" class="block text-base font-medium text-gray-900 mb-2">Number of Seats</label>
                      <input
                        type="number"
                        id="seats"
                        v-model="newTable.seats"
                        class="block w-full px-4 py-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                      />
                    </div>

                    <div>
                      <label for="location" class="block text-base font-medium text-gray-900 mb-2">Location (Optional)</label>
                      <input
                        type="text"
                        id="location"
                        v-model="newTable.location"
                        class="block w-full px-4 py-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        placeholder="e.g., Outdoor Patio"
                      />
                    </div>
                  </div>

                  <div class="mt-8 flex justify-end space-x-4">
                    <button
                      type="button"
                      class="px-6 py-3 rounded-md border border-gray-300 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                      @click="showAddTableModal = false"
                    >
                      Cancel
                    </button>
                    <button
                      type="button"
                      class="px-6 py-3 rounded-md border border-transparent bg-[#FF6B00] text-base font-medium text-white hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                      @click="addTable"
                    >
                      Add Table
                    </button>
                  </div>
                </DialogPanel>
              </TransitionChild>
            </div>
          </div>
        </Dialog>
      </TransitionRoot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { QrCodeIcon, TrashIcon, PlusIcon, XMarkIcon } from '@heroicons/vue/24/outline'

definePageMeta({
  layout: 'admin'
})

// Mock data for tables
const tables = ref([
  {
    id: 'dl5tfc685c8-1Qpu1vCnn',
    name: 'Table T01',
    seats: 4,
    status: 'available'
  },
  {
    id: 'OgGeMZ2XeNDiHHNkRW0e7',
    name: 'Table T02',
    seats: 2,
    status: 'available'
  },
  {
    id: 'U52pt0ZLrXj22FpCpCTmX',
    name: 'Table T254',
    seats: 4,
    status: 'available'
  },
  {
    id: '5aPLZh0sb1TDoaU5iyxU2',
    name: 'Table T356',
    seats: 6,
    location: 'Outdoor Patio',
    status: 'available'
  }
])

// Modal state and new table data
const showAddTableModal = ref(false)
const newTable = ref({
  number: '',
  seats: 4,
  location: ''
})

// Functions
const addTable = () => {
  // Add table logic here
  const table = {
    id: Math.random().toString(36).substring(7),
    name: `Table ${newTable.value.number}`,
    seats: newTable.value.seats,
    location: newTable.value.location,
    status: 'available'
  }
  tables.value.push(table)
  showAddTableModal.value = false
  newTable.value = { number: '', seats: 4, location: '' }
}

const downloadQR = (tableId: string) => {
  // Download QR code logic here
  console.log('Downloading QR code for table:', tableId)
}

const deleteTable = (tableId: string) => {
  // Delete table logic here
  tables.value = tables.value.filter(table => table.id !== tableId)
}
</script> 