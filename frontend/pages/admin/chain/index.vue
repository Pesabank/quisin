<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-semibold text-gray-900">Chain Management</h1>
        <button
          @click="showAddLocationModal = true"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
        >
          <PlusIcon class="h-5 w-5 mr-1" />
          Add Location
        </button>
      </div>

      <!-- Locations Grid -->
      <div class="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div v-for="location in locations" :key="location.id" class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-6">
            <div class="flex justify-between items-start">
              <div>
                <h3 class="text-lg font-medium text-gray-900">{{ location.name }}</h3>
                <p class="mt-1 text-sm text-gray-500">{{ location.address }}</p>
                <p class="mt-1 text-sm text-gray-500">{{ location.phone }}</p>
              </div>
              <span
                :class="[
                  'px-2 py-1 text-xs font-medium rounded-full',
                  location.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                ]"
              >
                {{ location.status }}
              </span>
            </div>
            <div class="mt-4 flex justify-end space-x-3">
              <button
                @click="editLocation(location)"
                class="inline-flex items-center text-sm text-[#FF6B00] hover:text-[#e66000]"
              >
                <PencilIcon class="h-5 w-5" />
              </button>
              <button
                @click="deleteLocation(location.id)"
                class="inline-flex items-center text-sm text-red-600 hover:text-red-700"
              >
                <TrashIcon class="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Add Location Modal -->
      <TransitionRoot appear :show="showAddLocationModal" as="template">
        <Dialog as="div" class="relative z-10" @close="showAddLocationModal = false">
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
            <div class="flex min-h-full items-center justify-center p-4">
              <TransitionChild
                as="template"
                enter="duration-300 ease-out"
                enter-from="opacity-0 scale-95"
                enter-to="opacity-100 scale-100"
                leave="duration-200 ease-in"
                leave-from="opacity-100 scale-100"
                leave-to="opacity-0 scale-95"
              >
                <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-lg bg-white p-6 text-left align-middle shadow-xl transition-all">
                  <div class="flex justify-between items-center mb-6">
                    <DialogTitle as="h3" class="text-2xl font-semibold text-gray-900">
                      {{ isEditing ? 'Edit Location' : 'Add New Location' }}
                    </DialogTitle>
                    <button @click="closeModal" class="text-gray-400 hover:text-gray-500">
                      <XMarkIcon class="h-6 w-6" />
                    </button>
                  </div>

                  <div class="space-y-6">
                    <div>
                      <label class="block text-lg font-medium text-gray-700 mb-2">Location Name</label>
                      <input
                        type="text"
                        v-model="locationForm.name"
                        class="mt-1 block w-full h-14 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-lg px-4"
                        placeholder="e.g., Downtown Branch"
                      />
                    </div>

                    <div>
                      <label class="block text-lg font-medium text-gray-700 mb-2">Address</label>
                      <input
                        type="text"
                        v-model="locationForm.address"
                        class="mt-1 block w-full h-14 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-lg px-4"
                        placeholder="Full address"
                      />
                    </div>

                    <div>
                      <label class="block text-lg font-medium text-gray-700 mb-2">Phone</label>
                      <input
                        type="text"
                        v-model="locationForm.phone"
                        class="mt-1 block w-full h-14 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-lg px-4"
                        placeholder="Phone number"
                      />
                    </div>

                    <div>
                      <label class="block text-lg font-medium text-gray-700 mb-2">Status</label>
                      <select
                        v-model="locationForm.status"
                        class="mt-1 block w-full h-14 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-lg px-4"
                      >
                        <option value="active">Active</option>
                        <option value="inactive">Inactive</option>
                      </select>
                    </div>
                  </div>

                  <div class="mt-8 flex justify-end space-x-4">
                    <button
                      type="button"
                      class="inline-flex justify-center rounded-lg border border-gray-300 bg-white px-6 py-3 text-base font-medium text-gray-700 hover:bg-gray-50"
                      @click="closeModal"
                    >
                      Cancel
                    </button>
                    <button
                      type="button"
                      class="inline-flex justify-center rounded-lg border border-transparent bg-[#FF6B00] px-6 py-3 text-base font-medium text-white hover:bg-[#e66000]"
                      @click="saveLocation"
                    >
                      {{ isEditing ? 'Update' : 'Add' }} Location
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
import { PlusIcon, PencilIcon, TrashIcon, XMarkIcon } from '@heroicons/vue/24/outline'

definePageMeta({
  layout: 'admin'
})

interface Location {
  id: string
  name: string
  address: string
  phone: string
  status: 'active' | 'inactive'
}

// Mock data
const locations = ref<Location[]>([
  {
    id: '1',
    name: 'Downtown Branch',
    address: '123 Main St, City',
    phone: '(555) 123-4567',
    status: 'active'
  },
  {
    id: '2',
    name: 'Westside Location',
    address: '456 West Ave, City',
    phone: '(555) 987-6543',
    status: 'active'
  }
])

const showAddLocationModal = ref(false)
const isEditing = ref(false)
const locationForm = ref({
  id: '',
  name: '',
  address: '',
  phone: '',
  status: 'active' as const
})

const editLocation = (location: Location) => {
  isEditing.value = true
  locationForm.value = { ...location }
  showAddLocationModal.value = true
}

const closeModal = () => {
  showAddLocationModal.value = false
  isEditing.value = false
  locationForm.value = {
    id: '',
    name: '',
    address: '',
    phone: '',
    status: 'active'
  }
}

const saveLocation = () => {
  if (isEditing.value) {
    const index = locations.value.findIndex(loc => loc.id === locationForm.value.id)
    if (index !== -1) {
      locations.value[index] = { ...locationForm.value }
    }
  } else {
    locations.value.push({
      ...locationForm.value,
      id: Math.random().toString(36).substring(7)
    })
  }
  closeModal()
}

const deleteLocation = (id: string) => {
  if (confirm('Are you sure you want to delete this location?')) {
    locations.value = locations.value.filter(location => location.id !== id)
  }
}
</script> 