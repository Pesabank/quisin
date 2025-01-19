<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Stock Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            Monitor and manage inventory levels for all menu items
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showBulkUpdateModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <ArrowPathIcon class="h-4 w-4 mr-2" />
            Bulk Update
          </button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="mt-6 border-b border-gray-200">
        <nav class="-mb-px flex space-x-8" aria-label="Tabs">
          <NuxtLink
            to="/admin/menu"
            class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Menu Items
          </NuxtLink>
          <NuxtLink
            to="/admin/menu/categories"
            class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Categories
          </NuxtLink>
          <NuxtLink
            to="/admin/menu/stock"
            class="border-[#FF6B00] text-[#FF6B00] whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Stock Management
          </NuxtLink>
        </nav>
      </div>

      <!-- Filters -->
      <div class="mt-6 flex justify-between gap-6 mb-8">
        <div class="flex-1">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search items..."
            class="block w-full h-12 px-4 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
          />
        </div>
        <select
          v-model="stockFilter"
          class="block w-48 h-12 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
        >
          <option value="all">All Stock Levels</option>
          <option value="low">Low Stock</option>
          <option value="out">Out of Stock</option>
        </select>
      </div>

      <!-- Stock Table -->
      <div class="mt-8">
        <div class="flex flex-col">
          <div class="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8">
            <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
              <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
                <table class="min-w-full divide-y divide-gray-300">
                  <thead class="bg-gray-50">
                    <tr>
                      <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                        Item Name
                      </th>
                      <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                        Category
                      </th>
                      <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                        Current Stock
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
                    <tr v-for="item in filteredItems" :key="item.id">
                      <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-6">
                        {{ item.name }}
                      </td>
                      <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                        {{ item.category }}
                      </td>
                      <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                        {{ item.stock }}
                      </td>
                      <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                        <span
                          :class="{
                            'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium': true,
                            'bg-green-100 text-green-800': item.stock > item.lowStockThreshold,
                            'bg-yellow-100 text-yellow-800': item.stock <= item.lowStockThreshold && item.stock > 0,
                            'bg-red-100 text-red-800': item.stock === 0
                          }"
                        >
                          {{ getStockStatus(item) }}
                        </span>
                      </td>
                      <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                        <button
                          @click="updateStock(item)"
                          class="text-[#FF6B00] hover:text-[#e66000] mr-4"
                        >
                          Update Stock
                        </button>
                        <button
                          @click="editThreshold(item)"
                          class="text-gray-600 hover:text-gray-900"
                        >
                          Edit Threshold
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Update Stock Modal -->
      <UpdateStockModal
        :show="showUpdateStockModal"
        :initial-stock="selectedItem?.stock || 0"
        @close="showUpdateStockModal = false"
        @save="saveStockUpdate"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ArrowPathIcon } from '@heroicons/vue/24/outline'
import UpdateStockModal from '@/components/menu/UpdateStockModal.vue'

definePageMeta({
  layout: 'admin'
})

interface MenuItem {
  id: string
  name: string
  category: string
  stock: number
  lowStockThreshold: number
}

// State
const searchQuery = ref('')
const stockFilter = ref('all')
const showUpdateStockModal = ref(false)
const showBulkUpdateModal = ref(false)
const selectedItem = ref<MenuItem | null>(null)

// Mock data
const items = ref<MenuItem[]>([
  {
    id: '1',
    name: 'Classic Burger',
    category: 'Main Course',
    stock: 15,
    lowStockThreshold: 10
  },
  {
    id: '2',
    name: 'Caesar Salad',
    category: 'Appetizers',
    stock: 8,
    lowStockThreshold: 5
  },
  {
    id: '3',
    name: 'Chocolate Cake',
    category: 'Desserts',
    stock: 0,
    lowStockThreshold: 3
  }
])

// Computed
const filteredItems = computed(() => {
  let filtered = items.value

  if (searchQuery.value) {
    const search = searchQuery.value.toLowerCase()
    filtered = filtered.filter(item =>
      item.name.toLowerCase().includes(search)
    )
  }

  if (stockFilter.value === 'low') {
    filtered = filtered.filter(item => item.stock <= item.lowStockThreshold && item.stock > 0)
  } else if (stockFilter.value === 'out') {
    filtered = filtered.filter(item => item.stock === 0)
  }

  return filtered
})

// Methods
const getStockStatus = (item: MenuItem) => {
  if (item.stock === 0) return 'Out of Stock'
  if (item.stock <= item.lowStockThreshold) return 'Low Stock'
  return 'In Stock'
}

const updateStock = (item: MenuItem) => {
  selectedItem.value = item
  showUpdateStockModal.value = true
}

const saveStockUpdate = (formData: { newStock: number; reason: string; notes: string }) => {
  if (selectedItem.value) {
    selectedItem.value.stock = formData.newStock
    // TODO: API call to update stock
  }
  showUpdateStockModal.value = false
}

const editThreshold = (item: MenuItem) => {
  // TODO: Implement threshold editing
}
</script> 