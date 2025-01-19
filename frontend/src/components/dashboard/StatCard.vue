<script setup lang="ts">
interface StatCardProps {
  title: string
  value: number | string
  icon?: string
  color?: string
  description?: string
}

const props = withDefaults(defineProps<StatCardProps>(), {
  color: '#FF6B00',
  icon: 'chart-line',
  description: ''
})
</script>

<template>
  <div class="bg-white p-6 rounded-lg shadow-md flex items-center">
    <div class="flex-grow">
      <h2 class="text-xl font-semibold" :style="{ color: color }">
        {{ title }}
      </h2>
      <p class="text-4xl font-bold text-gray-800 mt-2">
        {{ value }}
      </p>
      <p v-if="description" class="text-sm text-gray-500 mt-1">
        {{ description }}
      </p>
    </div>
    <div v-if="icon" class="ml-4 opacity-50">
      <!-- Placeholder for icon, can be replaced with actual icon component -->
      <i :class="`fas fa-${icon} text-3xl`" :style="{ color }"></i>
    </div>
  </div>
</template>

<style scoped>
.fas {
  opacity: 0.6;
}
</style>
