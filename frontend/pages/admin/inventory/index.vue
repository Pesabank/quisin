<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Inventory Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            Manage your restaurant's inventory, track stock levels, and set reorder alerts.
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showAddItemModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Item
          </button>
        </div>
      </div>

      <!-- Stats -->
      <div class="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-3">
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <ExclamationTriangleIcon class="h-6 w-6 text-red-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Low Stock Items</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ lowStockItems.length }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <ArchiveBoxIcon class="h-6 w-6 text-[#FF6B00]" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Items</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">{{ inventoryItems.length }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CurrencyDollarIcon class="h-6 w-6 text-green-500" />
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Value</dt>
                  <dd class="flex items-baseline">
                    <div class="text-2xl font-semibold text-gray-900">${{ totalValue.toFixed(2) }}</div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Search and Filters -->
      <div class="mt-8 flex flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0">
        <div class="flex-1">
          <div class="relative rounded-md shadow-sm">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
            </div>
            <input
              type="text"
              v-model="searchQuery"
              class="block w-full pl-10 rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
              placeholder="Search inventory items..."
            />
          </div>
        </div>
        <div class="w-full sm:w-48">
          <select
            v-model="stockFilter"
            class="block w-full rounded-md border-gray-300 focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
          >
            <option value="all">All Stock Levels</option>
            <option value="low">Low Stock</option>
            <option value="normal">Normal Stock</option>
            <option value="high">High Stock</option>
          </select>
        </div>
      </div>

      <!-- Inventory Table -->
      <div class="mt-8 flex flex-col">
        <div class="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
            <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table class="min-w-full divide-y divide-gray-300">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                      Item Name
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Quantity
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Unit
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Status
                    </th>
                    <th scope="col" class="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                      Last Updated
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
                      {{ item.quantity }}
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ item.unit }}
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm">
                      <span
                        :class="[
                          'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium',
                          getStockLevelClass(item)
                        ]"
                      >
                        {{ getStockLevelText(item) }}
                      </span>
                    </td>
                    <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                      {{ formatDate(item.updatedAt) }}
                    </td>
                    <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                      <div class="flex justify-end space-x-2">
                        <button
                          @click="editItem(item)"
                          class="text-[#FF6B00] hover:text-[#e66000]"
                        >
                          Edit
                        </button>
                        <button
                          @click="adjustStock(item)"
                          class="text-blue-600 hover:text-blue-900"
                        >
                          Adjust Stock
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

    <!-- Add/Edit Item Modal -->
    <TransitionRoot appear :show="showAddItemModal" as="template">
      <Dialog as="div" class="relative z-10" @close="showAddItemModal = false">
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
              <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
                <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                  {{ editingItem ? 'Edit Item' : 'Add New Item' }}
                </DialogTitle>

                <form @submit.prevent="saveItem" class="mt-4 space-y-4">
                  <div>
                    <label for="name" class="block text-base font-medium text-gray-700">
                      Item Name
                    </label>
                    <input
                      type="text"
                      id="name"
                      v-model="itemForm.name"
                      class="mt-1 block w-full h-16 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-xl px-4"
                      required
                    />
                  </div>

                  <div>
                    <label for="description" class="block text-base font-medium text-gray-700">
                      Description
                    </label>
                    <textarea
                      id="description"
                      v-model="itemForm.description"
                      rows="4"
                      class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-xl px-4 py-3"
                    />
                  </div>

                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <label for="quantity" class="block text-base font-medium text-gray-700">
                        Quantity
                      </label>
                      <input
                        type="number"
                        id="quantity"
                        v-model="itemForm.quantity"
                        step="0.01"
                        class="mt-1 block w-full h-16 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-xl px-4"
                        required
                      />
                    </div>

                    <div>
                      <label for="unit" class="block text-base font-medium text-gray-700">
                        Unit
                      </label>
                      <select
                        id="unit"
                        v-model="itemForm.unit"
                        class="mt-1 block w-full h-16 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-xl px-4"
                        required
                      >
                        <option value="kg">Kilograms (kg)</option>
                        <option value="g">Grams (g)</option>
                        <option value="l">Liters (l)</option>
                        <option value="ml">Milliliters (ml)</option>
                        <option value="pcs">Pieces (pcs)</option>
                      </select>
                    </div>
                  </div>

                  <div>
                    <label for="minThreshold" class="block text-base font-medium text-gray-700">
                      Minimum Threshold
                    </label>
                    <input
                      type="number"
                      id="minThreshold"
                      v-model="itemForm.minThreshold"
                      step="0.01"
                      class="mt-1 block w-full h-16 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-xl px-4"
                      required
                    />
                  </div>

                  <div class="mt-6 flex justify-end space-x-3">
                    <button
                      type="button"
                      class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                      @click="showAddItemModal = false"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                    >
                      {{ editingItem ? 'Save Changes' : 'Add Item' }}
                    </button>
                  </div>
                </form>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Adjust Stock Modal -->
    <TransitionRoot appear :show="showAdjustStockModal" as="template">
      <Dialog as="div" class="relative z-10" @close="showAdjustStockModal = false">
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
              <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
                <DialogTitle as="h3" class="text-xl font-medium leading-6 text-gray-900">
                  Adjust Stock Level
                </DialogTitle>

                <form @submit.prevent="saveStockAdjustment" class="mt-6">
                  <div class="space-y-6">
                    <div>
                      <p class="text-lg text-gray-500">
                        Current stock level: {{ selectedItem?.quantity }} {{ selectedItem?.unit }}
                      </p>
                    </div>

                    <div>
                      <label for="adjustment" class="block text-lg font-medium text-gray-700">
                        Adjustment Type
                      </label>
                      <select
                        id="adjustment"
                        v-model="stockAdjustment.type"
                        class="mt-2 block w-full h-20 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-2xl px-4"
                      >
                        <option value="add">Add Stock</option>
                        <option value="remove">Remove Stock</option>
                        <option value="set">Set New Level</option>
                      </select>
                    </div>

                    <div>
                      <label for="amount" class="block text-lg font-medium text-gray-700">
                        Amount ({{ selectedItem?.unit }})
                      </label>
                      <input
                        type="number"
                        id="amount"
                        v-model="stockAdjustment.amount"
                        step="0.01"
                        class="mt-2 block w-full h-20 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-2xl px-4"
                        required
                      />
                    </div>

                    <div>
                      <label for="reason" class="block text-lg font-medium text-gray-700">
                        Reason
                      </label>
                      <textarea
                        id="reason"
                        v-model="stockAdjustment.reason"
                        rows="4"
                        class="mt-2 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-2xl px-4 py-3"
                        required
                      />
                    </div>
                  </div>

                  <div class="mt-8 flex justify-end space-x-3">
                    <button
                      type="button"
                      class="px-6 py-3 border border-gray-300 rounded-md text-lg font-medium text-gray-700 bg-white hover:bg-gray-50"
                      @click="showAdjustStockModal = false"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      class="px-6 py-3 border border-transparent rounded-md shadow-sm text-lg font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                    >
                      Save Adjustment
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
import { ref, computed } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import {
  PlusIcon,
  MagnifyingGlassIcon,
  ExclamationTriangleIcon,
  ArchiveBoxIcon,
  CurrencyDollarIcon
} from '@heroicons/vue/24/outline'
import { format } from 'date-fns'

interface InventoryItem {
  id: string
  name: string
  description?: string
  quantity: number
  unit: string
  minThreshold: number
  updatedAt: Date
}

// State
const inventoryItems = ref<InventoryItem[]>([])
const searchQuery = ref('')
const stockFilter = ref('all')
const showAddItemModal = ref(false)
const showAdjustStockModal = ref(false)
const editingItem = ref<InventoryItem | null>(null)
const selectedItem = ref<InventoryItem | null>(null)

const itemForm = ref({
  name: '',
  description: '',
  quantity: 0,
  unit: 'kg',
  minThreshold: 0
})

const stockAdjustment = ref({
  type: 'add',
  amount: 0,
  reason: ''
})

// Computed
const filteredItems = computed(() => {
  return inventoryItems.value
    .filter(item => {
      // Search filter
      if (searchQuery.value) {
        const search = searchQuery.value.toLowerCase()
        return (
          item.name.toLowerCase().includes(search) ||
          item.description?.toLowerCase().includes(search) ||
          item.unit.toLowerCase().includes(search)
        )
      }
      
      // Stock level filter
      if (stockFilter.value !== 'all') {
        const level = getStockLevel(item)
        return stockFilter.value === level
      }
      
      return true
    })
    .sort((a, b) => a.name.localeCompare(b.name))
})

const lowStockItems = computed(() => {
  return inventoryItems.value.filter(item => item.quantity <= item.minThreshold)
})

const totalValue = computed(() => {
  // TODO: Add item cost/value to calculate total inventory value
  return 0
})

// Methods
const getStockLevel = (item: InventoryItem) => {
  if (item.quantity <= item.minThreshold) return 'low'
  if (item.quantity <= item.minThreshold * 2) return 'normal'
  return 'high'
}

const getStockLevelClass = (item: InventoryItem) => {
  const level = getStockLevel(item)
  return {
    low: 'bg-red-100 text-red-800',
    normal: 'bg-yellow-100 text-yellow-800',
    high: 'bg-green-100 text-green-800'
  }[level]
}

const getStockLevelText = (item: InventoryItem) => {
  const level = getStockLevel(item)
  return {
    low: 'Low Stock',
    normal: 'Normal',
    high: 'Well Stocked'
  }[level]
}

const formatDate = (date: Date) => {
  return format(new Date(date), 'MMM d, yyyy HH:mm')
}

const editItem = (item: InventoryItem) => {
  editingItem.value = item
  itemForm.value = {
    name: item.name,
    description: item.description || '',
    quantity: item.quantity,
    unit: item.unit,
    minThreshold: item.minThreshold
  }
  showAddItemModal.value = true
}

const adjustStock = (item: InventoryItem) => {
  selectedItem.value = item
  stockAdjustment.value = {
    type: 'add',
    amount: 0,
    reason: ''
  }
  showAdjustStockModal.value = true
}

const saveItem = async () => {
  try {
    if (editingItem.value) {
      // Update existing item
      // TODO: API call
    } else {
      // Add new item
      // TODO: API call
    }
    
    showAddItemModal.value = false
    editingItem.value = null
    itemForm.value = {
      name: '',
      description: '',
      quantity: 0,
      unit: 'kg',
      minThreshold: 0
    }
  } catch (err) {
    console.error('Failed to save item:', err)
    // TODO: Show error notification
  }
}

const saveStockAdjustment = async () => {
  try {
    if (!selectedItem.value) return

    const newQuantity = stockAdjustment.value.type === 'set'
      ? stockAdjustment.value.amount
      : stockAdjustment.value.type === 'add'
        ? selectedItem.value.quantity + stockAdjustment.value.amount
        : selectedItem.value.quantity - stockAdjustment.value.amount

    // TODO: API call to update stock level

    showAdjustStockModal.value = false
    selectedItem.value = null
    stockAdjustment.value = {
      type: 'add',
      amount: 0,
      reason: ''
    }
  } catch (err) {
    console.error('Failed to adjust stock:', err)
    // TODO: Show error notification
  }
}

// Lifecycle
onMounted(async () => {
  try {
    // TODO: Fetch inventory items from API
  } catch (err) {
    console.error('Failed to fetch inventory items:', err)
    // TODO: Show error notification
  }
})
</script> 