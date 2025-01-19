<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 text-center">
      <h1 class="text-6xl font-bold text-gray-900">{{ props.error?.statusCode }}</h1>
      <h2 class="mt-4 text-2xl font-medium text-gray-600">
        {{ props.error?.message }}
      </h2>
      <div class="mt-8">
        <button
          @click="handleError"
          class="inline-flex items-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
        >
          Back to Home
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  error: Object
})

function handleError() {
  clearError({ redirect: '/' })
}
</script>
