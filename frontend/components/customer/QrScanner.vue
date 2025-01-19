<template>
  <div class="flex flex-col items-center justify-center p-4">
    <h2 class="text-2xl font-bold mb-4">Welcome to {{ restaurantName }}</h2>
    <p class="text-lg mb-6">Table {{ tableNumber }}</p>
    
    <div class="space-y-4">
      <button
        @click="hailWaiter"
        class="w-full px-6 py-3 bg-[#FF6B00] text-white rounded-lg hover:bg-[#e66000]"
      >
        Hail Waiter
      </button>
      
      <button
        @click="viewMenu"
        class="w-full px-6 py-3 bg-white border-2 border-[#FF6B00] text-[#FF6B00] rounded-lg hover:bg-gray-50"
      >
        View Menu
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  restaurantName: string
  tableNumber: string
}>()

const emit = defineEmits<{
  (e: 'hailWaiter'): void
  (e: 'viewMenu'): void
}>()

function hailWaiter() {
  emit('hailWaiter')
}

function viewMenu() {
  emit('viewMenu')
}
</script> 