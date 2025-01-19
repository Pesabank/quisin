<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="sm:flex sm:items-center">
        <div class="sm:flex-auto">
          <h1 class="text-3xl font-bold text-gray-900">Menu Management</h1>
          <p class="mt-2 text-sm text-gray-700">
            Manage your restaurant's menu items, categories, and stock levels
          </p>
        </div>
        <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            @click="showAddDishModal = true"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
          >
            <PlusIcon class="h-4 w-4 mr-2" />
            Add Dish
          </button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="mt-6 border-b border-gray-200">
        <nav class="-mb-px flex space-x-8" aria-label="Tabs">
          <NuxtLink
            to="/admin/menu"
            class="border-[#FF6B00] text-[#FF6B00] whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
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
            class="border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm"
          >
            Stock Management
          </NuxtLink>
        </nav>
      </div>

      <!-- Search and Filter -->
      <div class="mt-6 flex justify-between gap-6 mb-8">
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

      <!-- Edit Dish Modal -->
      <TransitionRoot appear :show="showEditDishModal" as="template">
        <Dialog as="div" class="relative z-10" @close="closeEditModal">
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
                <DialogPanel class="w-full max-w-2xl transform overflow-hidden rounded-lg bg-white p-6 text-left align-middle shadow-xl transition-all">
                  <div class="flex justify-between items-center mb-6">
                    <DialogTitle as="h3" class="text-2xl font-semibold text-gray-900">
                      Edit Dish
                    </DialogTitle>
                    <button @click="closeEditModal" class="text-gray-400 hover:text-gray-500">
                      <XMarkIcon class="h-6 w-6" />
                    </button>
                  </div>

                  <form @submit.prevent="saveDishEdit" class="space-y-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">Name</label>
                      <input
                        type="text"
                        v-model="editForm.name"
                        class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        required
                      />
                    </div>

                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                      <textarea
                        v-model="editForm.description"
                        rows="3"
                        class="block w-full px-3 py-2 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        required
                      ></textarea>
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Category</label>
                        <select
                          v-model="editForm.category"
                          class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          required
                        >
                          <option v-for="category in categories" :key="category" :value="category">
                            {{ category }}
                          </option>
                        </select>
                      </div>

                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Price (KSH)</label>
                        <input
                          type="number"
                          v-model="editForm.price"
                          step="0.01"
                          min="0"
                          class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          required
                        />
                      </div>
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Stock</label>
                        <input
                          type="number"
                          v-model="editForm.stock"
                          min="0"
                          class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          required
                        />
                      </div>

                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Low Stock Threshold</label>
                        <input
                          type="number"
                          v-model="editForm.lowStockThreshold"
                          min="0"
                          class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          required
                        />
                      </div>
                    </div>

                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
                      <input
                        type="text"
                        v-model="editForm.image"
                        class="block w-full h-12 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                        placeholder="https://..."
                      />
                    </div>

                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-2">Ingredients</label>
                      <div class="space-y-2">
                        <div v-for="(ingredient, index) in editForm.ingredients" :key="index" class="flex gap-3">
                          <input
                            type="text"
                            v-model="ingredient.name"
                            placeholder="Ingredient name"
                            class="flex-1 h-10 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          />
                          <input
                            type="number"
                            v-model="ingredient.quantity"
                            placeholder="Qty"
                            class="w-24 h-10 px-3 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          />
                          <select
                            v-model="ingredient.unit"
                            class="w-24 h-10 px-2 rounded-md border-gray-300 shadow-sm focus:border-[#FF6B00] focus:ring-[#FF6B00] text-base"
                          >
                            <option value="g">g</option>
                            <option value="kg">kg</option>
                            <option value="ml">ml</option>
                            <option value="l">l</option>
                            <option value="pcs">pcs</option>
                          </select>
                          <button
                            type="button"
                            @click="removeIngredient(index)"
                            class="p-2 text-red-600 hover:text-red-800 rounded-md hover:bg-red-50"
                          >
                            <XMarkIcon class="h-5 w-5" />
                          </button>
                        </div>
                      </div>
                      <button
                        type="button"
                        @click="addIngredient"
                        class="mt-2 inline-flex items-center px-3 py-1.5 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
                      >
                        <PlusIcon class="h-4 w-4 mr-1" />
                        Add Ingredient
                      </button>
                    </div>

                    <div class="flex flex-wrap gap-4">
                      <div class="flex items-center">
                        <input
                          type="checkbox"
                          v-model="editForm.isVegan"
                          class="h-4 w-4 text-[#FF6B00] border-gray-300 rounded focus:ring-[#FF6B00]"
                        />
                        <label class="ml-2 text-sm text-gray-700">Vegan</label>
                      </div>
                      <div class="flex items-center">
                        <input
                          type="checkbox"
                          v-model="editForm.isSpicy"
                          class="h-4 w-4 text-[#FF6B00] border-gray-300 rounded focus:ring-[#FF6B00]"
                        />
                        <label class="ml-2 text-sm text-gray-700">Spicy</label>
                      </div>
                      <div class="flex items-center">
                        <input
                          type="checkbox"
                          v-model="editForm.isGlutenFree"
                          class="h-4 w-4 text-[#FF6B00] border-gray-300 rounded focus:ring-[#FF6B00]"
                        />
                        <label class="ml-2 text-sm text-gray-700">Gluten Free</label>
                      </div>
                    </div>

                    <div class="mt-6 flex justify-end space-x-3">
                      <button
                        type="button"
                        class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                        @click="closeEditModal"
                      >
                        Cancel
                      </button>
                      <button
                        type="submit"
                        class="px-4 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#e66000]"
                      >
                        Save Changes
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  PlusIcon,
  PencilIcon,
  ClipboardDocumentIcon,
  XMarkIcon,
} from '@heroicons/vue/24/outline'
import UpdateStockModal from '@/components/menu/UpdateStockModal.vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'

definePageMeta({
  layout: 'admin'
})

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

interface Ingredient {
  name: string
  quantity: number
  unit: string
}

interface EditForm extends Omit<Dish, 'id' | 'imageError'> {
  ingredients: Ingredient[]
}

// State
const searchQuery = ref('')
const selectedCategory = ref('')
const showUpdateStockModal = ref(false)
const showAddDishModal = ref(false)
const selectedDish = ref<Dish | null>(null)
const showEditDishModal = ref(false)
const editForm = ref<EditForm>({
  name: '',
  description: '',
  category: '',
  price: 0,
  stock: 0,
  lowStockThreshold: 0,
  image: '',
  isVegan: false,
  isSpicy: false,
  isGlutenFree: false,
  ingredients: []
})

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

const addIngredient = () => {
  editForm.value.ingredients.push({
    name: '',
    quantity: 0,
    unit: 'g'
  })
}

const removeIngredient = (index: number) => {
  editForm.value.ingredients.splice(index, 1)
}

const editDish = (dish: Dish) => {
  editForm.value = {
    ...dish,
    ingredients: dish.ingredients || []
  }
  showEditDishModal.value = true
}

const closeEditModal = () => {
  showEditDishModal.value = false
  editForm.value = {
    name: '',
    description: '',
    category: '',
    price: 0,
    stock: 0,
    lowStockThreshold: 0,
    image: '',
    isVegan: false,
    isSpicy: false,
    isGlutenFree: false,
    ingredients: []
  }
}

const saveDishEdit = () => {
  const index = dishes.value.findIndex(d => d.id === editForm.value.id)
  if (index !== -1) {
    dishes.value[index] = { ...editForm.value }
  }
  closeEditModal()
  // TODO: API call to update dish
}
</script> 