<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Hero Section -->
    <div class="relative bg-gray-900 py-16">
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-gradient-to-br from-[#FF6B00] to-gray-900 opacity-80"></div>
      </div>
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">Our Menu</h1>
        <p class="mt-6 max-w-2xl mx-auto text-xl text-gray-200">
          Discover our delicious offerings
        </p>
      </div>
    </div>

    <!-- Menu Categories -->
    <div class="py-12 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex overflow-x-auto pb-4 hide-scrollbar">
          <div class="flex space-x-4">
            <button 
              v-for="category in categories" 
              :key="category.id"
              class="px-6 py-2 text-sm font-medium rounded-full border-2 border-[#FF6B00] text-[#FF6B00] hover:bg-[#FF6B00] hover:text-white transition-colors duration-200"
              :class="{ 'bg-[#FF6B00] text-white': selectedCategory === category.id }"
              @click="selectedCategory = category.id"
            >
              {{ category.name }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Menu Items -->
    <div class="py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="item in filteredItems" :key="item.id" class="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300">
            <div class="aspect-w-16 aspect-h-9">
              <img :src="item.image" :alt="item.name" class="w-full h-full object-cover">
            </div>
            <div class="p-6">
              <div class="flex justify-between items-start">
                <div>
                  <h3 class="text-xl font-semibold text-gray-900">{{ item.name }}</h3>
                  <p class="mt-2 text-gray-600">{{ item.description }}</p>
                </div>
                <span class="text-xl font-bold text-[#FF6B00]">${{ item.price.toFixed(2) }}</span>
              </div>
              <div class="mt-4">
                <button class="w-full bg-[#FF6B00] text-white py-2 rounded-md hover:bg-[#e66000] transition-colors duration-200">
                  Add to Order
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

definePageMeta({
  layout: 'default',
  auth: false
})

const selectedCategory = ref(1)

const categories = [
  { id: 1, name: 'All' },
  { id: 2, name: 'Appetizers' },
  { id: 3, name: 'Main Course' },
  { id: 4, name: 'Desserts' },
  { id: 5, name: 'Beverages' }
]

const menuItems = [
  {
    id: 1,
    categoryId: 2,
    name: 'Crispy Calamari',
    description: 'Tender calamari rings, lightly breaded and fried to perfection',
    price: 12.99,
    image: '/images/menu/calamari.jpg'
  },
  {
    id: 2,
    categoryId: 3,
    name: 'Grilled Salmon',
    description: 'Fresh Atlantic salmon with herbs and lemon butter sauce',
    price: 24.99,
    image: '/images/menu/salmon.jpg'
  },
  {
    id: 3,
    categoryId: 4,
    name: 'Chocolate Lava Cake',
    description: 'Warm chocolate cake with a molten center',
    price: 8.99,
    image: '/images/menu/lava-cake.jpg'
  },
  {
    id: 4,
    categoryId: 5,
    name: 'Craft Mojito',
    description: 'Fresh mint, lime, rum, and soda water',
    price: 9.99,
    image: '/images/menu/mojito.jpg'
  },
  {
    id: 5,
    categoryId: 2,
    name: 'Bruschetta',
    description: 'Toasted bread topped with tomatoes, garlic, and basil',
    price: 8.99,
    image: '/images/menu/bruschetta.jpg'
  },
  {
    id: 6,
    categoryId: 3,
    name: 'Beef Tenderloin',
    description: '8oz beef tenderloin with red wine reduction',
    price: 34.99,
    image: '/images/menu/beef.jpg'
  }
]

const filteredItems = computed(() => {
  if (selectedCategory.value === 1) return menuItems
  return menuItems.filter(item => item.categoryId === selectedCategory.value)
})
</script>

<style scoped>
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.hide-scrollbar::-webkit-scrollbar {
  display: none;
}

.aspect-w-16 {
  position: relative;
  padding-bottom: 56.25%;
}

.aspect-w-16 > * {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
</style> 