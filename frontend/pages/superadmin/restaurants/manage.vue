<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center sm:justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Restaurant Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            Manage all registered restaurants and their administrators
          </p>
        </div>
        <div class="mt-4 sm:mt-0">
          <button
            @click="router.push('/superadmin/add-restaurant/step1')"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-5 w-5 mr-2" />
            Add Restaurant
          </button>
        </div>
      </div>

      <!-- Restaurant List -->
      <div class="mt-8 flex flex-col">
        <div class="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
            <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table class="min-w-full divide-y divide-gray-300">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                      Restaurant
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Location
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Administrator
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Status
                    </th>
                    <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                      <span class="sr-only">Actions</span>
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                  <tr v-for="restaurant in restaurants" :key="restaurant.id">
                    <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm sm:pl-6">
                      <div class="flex items-center">
                        <div class="h-10 w-10 flex-shrink-0">
                          <img :src="restaurant.logo" :alt="restaurant.name" class="h-10 w-10 rounded-full" />
                        </div>
                        <div class="ml-4">
                          <div class="font-medium text-gray-900">{{ restaurant.name }}</div>
                          <div class="text-gray-500">ID: {{ restaurant.id }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ restaurant.location }}
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      <div class="flex items-center">
                        <div class="h-8 w-8 flex-shrink-0">
                          <img :src="restaurant.admin.avatar" :alt="restaurant.admin.name" class="h-8 w-8 rounded-full" />
                        </div>
                        <div class="ml-3">
                          <div class="font-medium text-gray-900">{{ restaurant.admin.name }}</div>
                          <div class="text-gray-500">{{ restaurant.admin.email }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm">
                      <span
                        :class="[
                          restaurant.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                          'inline-flex rounded-full px-2 text-xs font-semibold leading-5'
                        ]"
                      >
                        {{ restaurant.status.charAt(0).toUpperCase() + restaurant.status.slice(1) }}
                      </span>
                    </td>
                    <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                      <div class="flex justify-end space-x-3">
                        <button
                          @click="editRestaurant(restaurant)"
                          class="text-[#FF6B00] hover:text-[#e66000]"
                        >
                          Edit
                        </button>
                        <button
                          @click="toggleRestaurantStatus(restaurant)"
                          :class="[
                            restaurant.status === 'active' ? 'text-red-600 hover:text-red-900' : 'text-green-600 hover:text-green-900'
                          ]"
                        >
                          {{ restaurant.status === 'active' ? 'Disable' : 'Enable' }}
                        </button>
                        <button
                          @click="deleteRestaurant(restaurant)"
                          class="text-red-600 hover:text-red-900"
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <TransitionRoot as="template" :show="showDeleteModal">
      <Dialog as="div" class="relative z-10" @close="closeDeleteModal">
        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 z-10 overflow-y-auto">
          <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <TransitionChild
              as="template"
              enter="ease-out duration-300"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="ease-in duration-200"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            >
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:p-6">
                <div>
                  <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-red-100">
                    <ExclamationTriangleIcon class="h-6 w-6 text-red-600" aria-hidden="true" />
                  </div>
                  <div class="mt-3 text-center sm:mt-5">
                    <DialogTitle as="h3" class="text-base font-semibold leading-6 text-gray-900">
                      Delete Restaurant
                    </DialogTitle>
                    <div class="mt-2">
                      <p class="text-sm text-gray-500">
                        Are you sure you want to delete this restaurant? This action cannot be undone.
                      </p>
                    </div>
                  </div>
                </div>

                <!-- Password Confirmation -->
                <div class="mt-5">
                  <label for="password" class="block text-sm font-medium text-gray-700">
                    Please enter your password to confirm
                  </label>
                  <div class="mt-1">
                    <input
                      type="password"
                      v-model="confirmPassword"
                      class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                      placeholder="Enter your password"
                    />
                  </div>
                </div>

                <div class="mt-5 sm:mt-6 sm:grid sm:grid-flow-row-dense sm:grid-cols-2 sm:gap-3">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-red-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-500 sm:col-start-2"
                    @click="confirmDelete"
                  >
                    Delete
                  </button>
                  <button
                    type="button"
                    class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:col-start-1 sm:mt-0"
                    @click="closeDeleteModal"
                  >
                    Cancel
                  </button>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Status Toggle Confirmation Modal -->
    <TransitionRoot as="template" :show="showStatusModal">
      <Dialog as="div" class="relative z-10" @close="closeStatusModal">
        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </TransitionChild>

        <div class="fixed inset-0 z-10 overflow-y-auto">
          <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <TransitionChild
              as="template"
              enter="ease-out duration-300"
              enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enter-to="opacity-100 translate-y-0 sm:scale-100"
              leave="ease-in duration-200"
              leave-from="opacity-100 translate-y-0 sm:scale-100"
              leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            >
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:p-6">
                <div>
                  <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-yellow-100">
                    <ExclamationTriangleIcon class="h-6 w-6 text-yellow-600" aria-hidden="true" />
                  </div>
                  <div class="mt-3 text-center sm:mt-5">
                    <DialogTitle as="h3" class="text-base font-semibold leading-6 text-gray-900">
                      {{ selectedRestaurant?.status === 'active' ? 'Disable' : 'Enable' }} Restaurant
                    </DialogTitle>
                    <div class="mt-2">
                      <p class="text-sm text-gray-500">
                        Are you sure you want to {{ selectedRestaurant?.status === 'active' ? 'disable' : 'enable' }} this restaurant?
                      </p>
                    </div>
                  </div>
                </div>

                <!-- Password Confirmation -->
                <div class="mt-5">
                  <label for="password" class="block text-sm font-medium text-gray-700">
                    Please enter your password to confirm
                  </label>
                  <div class="mt-1">
                    <input
                      type="password"
                      v-model="confirmPassword"
                      class="block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] sm:text-sm"
                      placeholder="Enter your password"
                    />
                  </div>
                </div>

                <div class="mt-5 sm:mt-6 sm:grid sm:grid-flow-row-dense sm:grid-cols-2 sm:gap-3">
                  <button
                    type="button"
                    class="inline-flex w-full justify-center rounded-md bg-[#FF6B00] px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-[#e66000] sm:col-start-2"
                    @click="confirmStatusToggle"
                  >
                    Confirm
                  </button>
                  <button
                    type="button"
                    class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:col-start-1 sm:mt-0"
                    @click="closeStatusModal"
                  >
                    Cancel
                  </button>
                </div>
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
import { useRouter } from 'nuxt/app'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { ExclamationTriangleIcon, PlusIcon } from '@heroicons/vue/24/outline'

interface Admin {
  id: string
  name: string
  email: string
  avatar: string
}

interface Restaurant {
  id: string
  name: string
  logo: string
  location: string
  status: 'active' | 'disabled'
  admin: Admin
}

const router = useRouter()
const showDeleteModal = ref(false)
const showStatusModal = ref(false)
const confirmPassword = ref('')
const selectedRestaurant = ref<Restaurant | null>(null)

// Mock data
const restaurants = ref<Restaurant[]>([
  {
    id: 'REST001',
    name: 'The Golden Spoon',
    logo: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4',
    location: 'New York, USA',
    status: 'active',
    admin: {
      id: 'ADM001',
      name: 'John Smith',
      email: 'john@goldenspoon.com',
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e'
    }
  },
  {
    id: 'REST002',
    name: 'Bella Italia',
    logo: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5',
    location: 'Rome, Italy',
    status: 'disabled',
    admin: {
      id: 'ADM002',
      name: 'Maria Romano',
      email: 'maria@bellaitalia.com',
      avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80'
    }
  }
])

const editRestaurant = (restaurant: Restaurant) => {
  router.push(`/superadmin/restaurants/${restaurant.id}/edit`)
}

const deleteRestaurant = (restaurant: Restaurant) => {
  selectedRestaurant.value = restaurant
  showDeleteModal.value = true
}

const toggleRestaurantStatus = (restaurant: Restaurant) => {
  selectedRestaurant.value = restaurant
  showStatusModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  confirmPassword.value = ''
  selectedRestaurant.value = null
}

const closeStatusModal = () => {
  showStatusModal.value = false
  confirmPassword.value = ''
  selectedRestaurant.value = null
}

const confirmDelete = async () => {
  if (!confirmPassword.value) {
    alert('Please enter your password')
    return
  }

  try {
    // Verify password and delete restaurant
    console.log('Deleting restaurant:', selectedRestaurant.value?.id)
    restaurants.value = restaurants.value.filter(r => r.id !== selectedRestaurant.value?.id)
    closeDeleteModal()
  } catch (error) {
    console.error('Error deleting restaurant:', error)
  }
}

const confirmStatusToggle = async () => {
  if (!confirmPassword.value) {
    alert('Please enter your password')
    return
  }

  try {
    // Verify password and toggle status
    const index = restaurants.value.findIndex(r => r.id === selectedRestaurant.value?.id)
    if (index !== -1) {
      restaurants.value[index].status = restaurants.value[index].status === 'active' ? 'disabled' : 'active'
    }
    closeStatusModal()
  } catch (error) {
    console.error('Error toggling restaurant status:', error)
  }
}
</script> 