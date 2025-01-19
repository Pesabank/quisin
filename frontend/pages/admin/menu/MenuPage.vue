<template>
  <div class="flex min-h-screen bg-gray-50">
    <!-- Sidebar -->
    <div class="w-64 bg-white shadow-lg">
      <div class="p-6">
        <h2 class="text-lg font-semibold text-gray-900">Menu Management</h2>
        <nav class="mt-6 space-y-2">
          <NuxtLink
            to="/admin/menu"
            class="flex items-center px-4 py-2 text-sm font-medium text-[#FF6B00] bg-orange-50 rounded-md"
          >
            Menu Items
          </NuxtLink>
          <NuxtLink
            to="/admin/menu/categories"
            class="flex items-center px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 rounded-md"
          >
            Categories
          </NuxtLink>
          <NuxtLink
            to="/admin/menu/stock"
            class="flex items-center px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 rounded-md"
          >
            Stock Management
          </NuxtLink>
        </nav>
      </div>
    </div>

    <!-- Main Content -->
    <div class="flex-1 min-w-0 overflow-hidden">
      <div class="py-6 px-8">
        <!-- Search and Filter -->
        <div class="flex justify-between gap-6 mb-8">
          <div class="flex-1">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="Search dishes..."
              class="block w-full h-12 px-4 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
            />
          </div>
          <select
            v-model="selectedCategory"
            class="block w-64 h-12 rounded-lg border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
          >
            <option value="">All Categories</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
          <div class="flex space-x-4">
            <button
              @click="showAddCategoryModal = true"
              class="inline-flex items-center px-6 py-3 text-base font-medium rounded-lg text-gray-700 bg-white border border-gray-300 hover:bg-gray-50"
            >
              Add Category
            </button>
            <button
              @click="showAddDishModal = true"
              class="inline-flex items-center px-6 py-3 text-base font-medium rounded-lg text-white bg-[#FF6B00] hover:bg-[#e66000]"
            >
              Add Dish
            </button>
          </div>
        </div>

        <!-- Menu Grid -->
        <div class="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          <div v-for="dish in filteredDishes" :key="dish.id" class="bg-white overflow-hidden shadow rounded-lg">
            <!-- Dish Card Content -->
            <div class="relative h-48">
              <img
                :src="dish.image"
                :alt="dish.name"
                @error="handleImageError"
                class="w-full h-full object-cover"
                :class="{ 'opacity-0': !dish.image || dish.imageError }"
              />
              <div
                v-if="!dish.image || dish.imageError"
                class="absolute inset-0 bg-gray-100 flex items-center justify-center"
              >
                <div class="text-center">
                  <div class="text-[#FF6B00] mb-2">
                    <svg class="mx-auto h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <span class="text-gray-500 text-sm">No Image Available</span>
                </div>
              </div>
              <div class="absolute top-4 right-4">
                <span class="text-lg font-semibold text-white bg-[#FF6B00] px-3 py-1 rounded-full shadow-lg">
                  KSH {{ formatPrice(dish.price) }}
                </span>
              </div>
            </div>
            <div class="p-6">
              <div class="flex justify-between items-start">
                <div class="flex-1">
                  <h3 class="text-lg font-medium text-gray-900">{{ dish.name }}</h3>
                  <p class="mt-1 text-sm text-gray-500">{{ dish.category }}</p>
                  <p class="mt-2 text-sm text-gray-500">{{ dish.description }}</p>
                  <div class="mt-2 flex flex-wrap gap-2">
                    <span v-if="dish.isVegan" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800">
                      Vegan
                    </span>
                    <span v-if="dish.isSpicy" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-red-100 text-red-800">
                      Spicy
                    </span>
                    <span v-if="dish.isGlutenFree" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-yellow-100 text-yellow-800">
                      Gluten Free
                    </span>
                  </div>
                </div>
                <span
                  :class="{
                    'inline-flex items-center px-2 py-1 rounded-full text-xs font-medium': true,
                    'bg-green-100 text-green-800': dish.stock > dish.lowStockThreshold,
                    'bg-yellow-100 text-yellow-800': dish.stock <= dish.lowStockThreshold && dish.stock > 0,
                    'bg-red-100 text-red-800': dish.stock === 0
                  }"
                >
                  {{ getStockStatus(dish) }}
                </span>
              </div>
              <div class="mt-4 flex justify-between items-center">
                <div class="text-sm text-gray-500">
                  Stock: {{ dish.stock }}
                </div>
                <div class="flex space-x-2">
                  <button
                    @click="updateStock(dish)"
                    class="text-[#FF6B00] hover:text-[#e66000] p-2 bg-orange-50 rounded-md transition-colors duration-200"
                    title="Update Stock"
                  >
                    <ClipboardDocumentIcon class="h-5 w-5" />
                  </button>
                  <button
                    @click="editDish(dish)"
                    class="text-[#FF6B00] hover:text-[#e66000] p-2 bg-orange-50 rounded-md transition-colors duration-200"
                    title="Edit Dish"
                  >
                    <PencilIcon class="h-5 w-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Update Stock Modal -->
        <UpdateStockModal
          :show="showUpdateStockModal"
          :initial-stock="selectedDish?.stock || 0"
          @close="showUpdateStockModal = false"
          @save="saveStockUpdate"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  XMarkIcon,
  PencilIcon,
  ClipboardDocumentIcon,
} from '@heroicons/vue/24/outline'
import UpdateStockModal from '@/components/menu/UpdateStockModal.vue'

interface Dish {
  id: string
  name: string
  category: string
  description: string
  price: number
  stock: number
  lowStockThreshold: number
  image?: string
  isVegan?: boolean
  isSpicy?: boolean
  isGlutenFree?: boolean
  imageError?: boolean
}

// State
const searchQuery = ref('')
const selectedCategory = ref('')
const showUpdateStockModal = ref(false)
const selectedDish = ref<Dish | null>(null)

// Mock data
const categories = ['Main Course', 'Appetizers', 'Desserts', 'Beverages']
const dishes = ref<Dish[]>([
  {
    id: '1',
    name: 'Classic Burger',
    category: 'Main Course',
    description: 'Juicy beef patty with fresh lettuce, tomatoes, and special sauce',
    price: 1299.00,
    stock: 15,
    lowStockThreshold: 10,
    image: '',
    isSpicy: false,
    isVegan: false,
    isGlutenFree: false
  },
  // Add more dishes...
])

// Computed
const filteredDishes = computed(() => {
  let filtered = dishes.value
  
  if (searchQuery.value) {
    const search = searchQuery.value.toLowerCase()
    filtered = filtered.filter(dish =>
      dish.name.toLowerCase().includes(search) ||
      dish.description.toLowerCase().includes(search)
    )
  }

  if (selectedCategory.value) {
    filtered = filtered.filter(dish => dish.category === selectedCategory.value)
  }

  return filtered
})

// Methods
const formatPrice = (price: number) => {
  return price.toFixed(2)
}

const getStockStatus = (dish: Dish) => {
  if (dish.stock === 0) return 'Out of Stock'
  if (dish.stock <= dish.lowStockThreshold) return 'Low Stock'
  return 'In Stock'
}

const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement
  const dish = dishes.value.find(d => d.image === target.src)
  if (dish) {
    dish.imageError = true
  }
}

const updateStock = (dish: Dish) => {
  selectedDish.value = dish
  showUpdateStockModal.value = true
}

const saveStockUpdate = (formData: { newStock: number; reason: string; notes: string }) => {
  if (selectedDish.value) {
    selectedDish.value.stock = formData.newStock
    // TODO: API call to update stock
  }
  showUpdateStockModal.value = false
}

const editDish = (dish: Dish) => {
  // TODO: Implement edit dish functionality
}
</script> 