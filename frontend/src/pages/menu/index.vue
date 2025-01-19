<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Menu Management</h1>
          <div class="flex items-center space-x-4">
            <button
              @click="openAddItemModal"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Add Menu Item
            </button>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Category Filter -->
      <div class="mb-6">
        <div class="flex space-x-2">
          <button
            v-for="category in categories"
            :key="category"
            @click="selectedCategory = category"
            class="px-4 py-2 rounded-md text-sm font-medium"
            :class="selectedCategory === category ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 hover:bg-gray-50'"
          >
            {{ category }}
          </button>
          <button
            @click="selectedCategory = 'all'"
            class="px-4 py-2 rounded-md text-sm font-medium"
            :class="selectedCategory === 'all' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 hover:bg-gray-50'"
          >
            All Items
          </button>
        </div>
      </div>

      <!-- Menu Items Grid -->
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="item in filteredMenuItems"
          :key="item.id"
          class="bg-white overflow-hidden shadow rounded-lg"
        >
          <div class="p-6">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-medium text-gray-900">{{ item.name }}</h3>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                {{ item.category }}
              </span>
            </div>
            <p class="mt-2 text-sm text-gray-500">{{ item.description }}</p>
            <div class="mt-4 flex items-center justify-between">
              <p class="text-lg font-semibold text-gray-900">${{ item.price.toFixed(2) }}</p>
              <div class="flex space-x-2">
                <button
                  @click="editItem(item)"
                  class="inline-flex items-center px-3 py-1.5 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  Edit
                </button>
                <button
                  @click="deleteItem(item)"
                  class="inline-flex items-center px-3 py-1.5 border border-red-300 text-sm font-medium rounded-md text-red-700 bg-white hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Add/Edit Item Modal -->
    <TransitionRoot appear :show="isModalOpen" as="template">
      <Dialog as="div" @close="closeModal" class="relative z-10">
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
              <DialogPanel class="relative transform overflow-hidden rounded-lg bg-white px-4 pb-4 pt-5 text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg sm:p-6">
                <form @submit.prevent="saveItem">
                  <div>
                    <div class="mt-3 sm:mt-5">
                      <DialogTitle as="h3" class="text-lg font-medium leading-6 text-gray-900">
                        {{ editingItem ? 'Edit Menu Item' : 'Add Menu Item' }}
                      </DialogTitle>
                      <div class="mt-6 space-y-6">
                        <div>
                          <label for="name" class="block text-sm font-medium text-gray-700">Name</label>
                          <input
                            type="text"
                            id="name"
                            v-model="itemForm.name"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                            required
                          />
                        </div>
                        <div>
                          <label for="category" class="block text-sm font-medium text-gray-700">Category</label>
                          <select
                            id="category"
                            v-model="itemForm.category"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                            required
                          >
                            <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
                          </select>
                        </div>
                        <div>
                          <label for="description" class="block text-sm font-medium text-gray-700">Description</label>
                          <textarea
                            id="description"
                            v-model="itemForm.description"
                            rows="3"
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                          ></textarea>
                        </div>
                        <div>
                          <label for="price" class="block text-sm font-medium text-gray-700">Price</label>
                          <div class="mt-1 relative rounded-md shadow-sm">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                              <span class="text-gray-500 sm:text-sm">$</span>
                            </div>
                            <input
                              type="number"
                              id="price"
                              v-model="itemForm.price"
                              min="0"
                              step="0.01"
                              class="pl-7 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                              required
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="mt-6 sm:mt-6 sm:grid sm:grid-flow-row-dense sm:grid-cols-2 sm:gap-3">
                    <button
                      type="submit"
                      class="inline-flex w-full justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2 text-base font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 sm:col-start-2 sm:text-sm"
                    >
                      {{ editingItem ? 'Save Changes' : 'Add Item' }}
                    </button>
                    <button
                      type="button"
                      @click="closeModal"
                      class="mt-3 inline-flex w-full justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-base font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 sm:col-start-1 sm:mt-0 sm:text-sm"
                    >
                      Cancel
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

interface MenuItem {
  id: string
  name: string
  description: string
  price: number
  category: string
}

// Mock data - replace with API calls
const menuItems = ref<MenuItem[]>([
  {
    id: '1',
    name: 'Margherita Pizza',
    description: 'Fresh tomatoes, mozzarella, basil',
    price: 12.99,
    category: 'Pizza'
  },
  {
    id: '2',
    name: 'Caesar Salad',
    description: 'Romaine lettuce, croutons, parmesan',
    price: 8.99,
    category: 'Salads'
  },
  {
    id: '3',
    name: 'Spaghetti Carbonara',
    description: 'Pasta with eggs, cheese, pancetta, and black pepper',
    price: 14.99,
    category: 'Pasta'
  }
])

const categories = ['Pizza', 'Pasta', 'Salads', 'Desserts', 'Beverages']
const selectedCategory = ref('all')
const isModalOpen = ref(false)
const editingItem = ref<MenuItem | null>(null)

const itemForm = ref({
  name: '',
  description: '',
  price: 0,
  category: categories[0]
})

const filteredMenuItems = computed(() => {
  if (selectedCategory.value === 'all') {
    return menuItems.value
  }
  return menuItems.value.filter(item => item.category === selectedCategory.value)
})

const openAddItemModal = () => {
  editingItem.value = null
  itemForm.value = {
    name: '',
    description: '',
    price: 0,
    category: categories[0]
  }
  isModalOpen.value = true
}

const editItem = (item: MenuItem) => {
  editingItem.value = item
  itemForm.value = { ...item }
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
  editingItem.value = null
}

const saveItem = async () => {
  try {
    if (editingItem.value) {
      // Update existing item
      const index = menuItems.value.findIndex(item => item.id === editingItem.value?.id)
      if (index !== -1) {
        menuItems.value[index] = {
          ...editingItem.value,
          ...itemForm.value
        }
      }
    } else {
      // Add new item
      menuItems.value.push({
        id: `${Date.now()}`,
        ...itemForm.value
      })
    }
    closeModal()
  } catch (error) {
    console.error('Error saving menu item:', error)
  }
}

const deleteItem = async (item: MenuItem) => {
  if (confirm('Are you sure you want to delete this menu item?')) {
    try {
      menuItems.value = menuItems.value.filter(i => i.id !== item.id)
    } catch (error) {
      console.error('Error deleting menu item:', error)
    }
  }
}
</script>
