<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Restaurants</h1>
          <p class="mt-2 text-sm text-gray-700">
            A list of all restaurants in the system including their details and status.
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none space-x-4">
          <button
            @click="exportData"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <ArrowDownTrayIcon class="h-4 w-4 mr-2" />
            Export
          </button>
          <button
            @click="$router.push('/superadmin/add-restaurant/step1')"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Restaurant
          </button>
        </div>
      </div>

      <!-- Filters -->
      <div class="mt-8 bg-white p-6 rounded-lg shadow">
        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          <!-- Search -->
          <div>
            <label for="search" class="block text-sm font-medium text-gray-700">Search</label>
            <div class="mt-1 relative rounded-md shadow-sm">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
              </div>
              <input
                type="text"
                name="search"
                id="search"
                v-model="filters.search"
                class="block w-full pl-10 rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
                placeholder="Search restaurants..."
              />
            </div>
          </div>

          <!-- Status Filter -->
          <div>
            <label for="status" class="block text-sm font-medium text-gray-700">Status</label>
            <select
              id="status"
              v-model="filters.status"
              class="mt-1 block w-full rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
            >
              <option value="">All Status</option>
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
          </div>

          <!-- Country Filter -->
          <div>
            <label for="country" class="block text-sm font-medium text-gray-700">Country</label>
            <select
              id="country"
              v-model="filters.country"
              class="mt-1 block w-full rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
            >
              <option value="">All Countries</option>
              <option v-for="country in countries" :key="country.code" :value="country.code">
                {{ country.name }}
              </option>
            </select>
          </div>

          <!-- Clear Filters -->
          <div class="flex items-end">
            <button
              @click="clearFilters"
              class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
            >
              <XMarkIcon class="h-4 w-4 mr-2" />
              Clear Filters
            </button>
          </div>
        </div>
      </div>

      <!-- Table -->
      <div class="mt-8 flex flex-col">
        <div class="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
            <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 rounded-lg">
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
                      Admin
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Status
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Currency
                    </th>
                    <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6">
                      <span class="sr-only">Actions</span>
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                  <tr v-for="restaurant in filteredRestaurants" :key="restaurant.id">
                    <td class="whitespace-nowrap py-4 pl-4 pr-3 sm:pl-6">
                      <div class="flex items-center">
                        <div class="h-10 w-10 flex-shrink-0">
                          <img :src="restaurant.logo" :alt="restaurant.name" class="h-10 w-10 rounded-full object-cover" />
                        </div>
                        <div class="ml-4">
                          <div class="font-medium text-gray-900">{{ restaurant.name }}</div>
                          <div class="text-gray-500">ID: {{ restaurant.id }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      <div>{{ restaurant.city }}</div>
                      <div>{{ getCountryName(restaurant.country) }}</div>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      <div class="font-medium text-gray-900">{{ restaurant.admin.name }}</div>
                      <div>{{ restaurant.admin.email }}</div>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm">
                      <span
                        :class="[
                          restaurant.status === 'active'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800',
                          'inline-flex rounded-full px-2 py-1 text-xs font-semibold leading-5'
                        ]"
                      >
                        {{ restaurant.status.charAt(0).toUpperCase() + restaurant.status.slice(1) }}
                      </span>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ restaurant.currency }}
                    </td>
                    <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                      <div class="flex justify-end space-x-2">
                        <button
                          @click="downloadCredentials(restaurant)"
                          class="text-[#FF6B00] hover:text-[#e66000]"
                          title="Download Credentials"
                        >
                          <ArrowDownTrayIcon class="h-5 w-5" />
                        </button>
                        <button
                          @click="toggleStatus(restaurant)"
                          :class="[
                            restaurant.status === 'active' ? 'text-red-600 hover:text-red-700' : 'text-green-600 hover:text-green-700'
                          ]"
                          :title="restaurant.status === 'active' ? 'Disable Restaurant' : 'Enable Restaurant'"
                        >
                          <PowerIcon class="h-5 w-5" />
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

      <!-- Pagination -->
      <div class="mt-6 flex items-center justify-between">
        <div class="flex-1 flex justify-between sm:hidden">
          <button
            @click="currentPage--"
            :disabled="currentPage === 1"
            class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Previous
          </button>
          <button
            @click="currentPage++"
            :disabled="currentPage === totalPages"
            class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Next
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              Showing
              <span class="font-medium">{{ paginationStart }}</span>
              to
              <span class="font-medium">{{ paginationEnd }}</span>
              of
              <span class="font-medium">{{ totalRestaurants }}</span>
              results
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <button
                @click="currentPage--"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">Previous</span>
                <ChevronLeftIcon class="h-5 w-5" />
              </button>
              <button
                v-for="page in displayedPages"
                :key="page"
                @click="currentPage = page"
                :class="[
                  page === currentPage
                    ? 'z-10 bg-[#FF6B00] border-[#FF6B00] text-white'
                    : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50',
                  'relative inline-flex items-center px-4 py-2 border text-sm font-medium'
                ]"
              >
                {{ page }}
              </button>
              <button
                @click="currentPage++"
                :disabled="currentPage === totalPages"
                class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">Next</span>
                <ChevronRightIcon class="h-5 w-5" />
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>

    <!-- Status Change Confirmation Modal -->
    <TransitionRoot appear :show="showStatusModal" as="template">
      <Dialog as="div" class="relative z-10" @close="showStatusModal = false">
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
                  Confirm Status Change
                </DialogTitle>
                <div class="mt-2">
                  <p class="text-sm text-gray-500">
                    Are you sure you want to {{ selectedRestaurant?.status === 'active' ? 'disable' : 'enable' }}
                    <span class="font-medium">{{ selectedRestaurant?.name }}</span>?
                    {{ selectedRestaurant?.status === 'active' 
                      ? 'This will prevent the restaurant from accepting new orders.'
                      : 'This will allow the restaurant to accept new orders.' }}
                  </p>
                </div>

                <div class="mt-6 flex justify-end space-x-4">
                  <button
                    type="button"
                    class="inline-flex justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
                    @click="showStatusModal = false"
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    :class="[
                      selectedRestaurant?.status === 'active'
                        ? 'bg-red-600 hover:bg-red-700'
                        : 'bg-green-600 hover:bg-green-700',
                      'inline-flex justify-center rounded-md border border-transparent px-4 py-2 text-sm font-medium text-white focus:outline-none focus:ring-2 focus:ring-offset-2'
                    ]"
                    @click="confirmStatusChange"
                  >
                    {{ selectedRestaurant?.status === 'active' ? 'Disable' : 'Enable' }}
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
import { ref, computed } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import {
  PlusIcon,
  ArrowDownTrayIcon,
  PowerIcon,
  MagnifyingGlassIcon,
  XMarkIcon,
  ChevronLeftIcon,
  ChevronRightIcon
} from '@heroicons/vue/24/outline'

// Mock data - Replace with API calls
const restaurants = ref([
  {
    id: '1',
    name: 'Italian Bistro',
    logo: 'https://images.unsplash.com/photo-1498837167922-ddd27525d352',
    city: 'New York',
    country: 'US',
    admin: {
      name: 'John Doe',
      email: 'john@italianbistro.com'
    },
    status: 'active',
    currency: 'USD'
  },
  {
    id: '2',
    name: 'Sushi Express',
    logo: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c',
    city: 'Los Angeles',
    country: 'US',
    admin: {
      name: 'Jane Smith',
      email: 'jane@sushiexpress.com'
    },
    status: 'active',
    currency: 'USD'
  },
  // Add more mock data
])

const countries = [
  { code: 'US', name: 'United States' },
  { code: 'GB', name: 'United Kingdom' },
  { code: 'CA', name: 'Canada' },
  // Add more countries
]

// Filters
const filters = ref({
  search: '',
  status: '',
  country: ''
})

// Pagination
const currentPage = ref(1)
const itemsPerPage = 10
const totalPages = computed(() => Math.ceil(filteredRestaurants.value.length / itemsPerPage))

// Status change modal
const showStatusModal = ref(false)
const selectedRestaurant = ref(null)

// Computed properties
const filteredRestaurants = computed(() => {
  return restaurants.value.filter(restaurant => {
    const matchesSearch = restaurant.name.toLowerCase().includes(filters.value.search.toLowerCase()) ||
                         restaurant.admin.name.toLowerCase().includes(filters.value.search.toLowerCase())
    const matchesStatus = !filters.value.status || restaurant.status === filters.value.status
    const matchesCountry = !filters.value.country || restaurant.country === filters.value.country
    
    return matchesSearch && matchesStatus && matchesCountry
  })
})

const paginatedRestaurants = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredRestaurants.value.slice(start, end)
})

const paginationStart = computed(() => ((currentPage.value - 1) * itemsPerPage) + 1)
const paginationEnd = computed(() => Math.min(currentPage.value * itemsPerPage, filteredRestaurants.value.length))
const totalRestaurants = computed(() => filteredRestaurants.value.length)

const displayedPages = computed(() => {
  const pages = []
  for (let i = 1; i <= totalPages.value; i++) {
    if (i === 1 || i === totalPages.value || (i >= currentPage.value - 1 && i <= currentPage.value + 1)) {
      pages.push(i)
    }
  }
  return pages
})

// Methods
const clearFilters = () => {
  filters.value = {
    search: '',
    status: '',
    country: ''
  }
}

const getCountryName = (code: string) => {
  return countries.find(country => country.code === code)?.name || code
}

const toggleStatus = (restaurant: any) => {
  selectedRestaurant.value = restaurant
  showStatusModal.value = true
}

const confirmStatusChange = async () => {
  if (selectedRestaurant.value) {
    // TODO: Call API to update status
    selectedRestaurant.value.status = selectedRestaurant.value.status === 'active' ? 'inactive' : 'active'
    showStatusModal.value = false
    selectedRestaurant.value = null
  }
}

const downloadCredentials = async (restaurant: any) => {
  // TODO: Implement credentials download
  console.log('Downloading credentials for:', restaurant.name)
}

const exportData = () => {
  // TODO: Implement data export
  console.log('Exporting data...')
}
</script>

<style scoped>
/* Add any component-specific styles here */
</style> 