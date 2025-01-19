<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full text-center">
      <h1 class="text-6xl font-bold text-gray-900">403</h1>
      <h2 class="mt-4 text-2xl font-medium text-gray-600">Access Denied</h2>
      <p class="mt-2 text-gray-500">
        You don't have permission to access this page
      </p>
      <button
        @click="goBack"
        class="mt-8 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
      >
        Go Back
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goBack() {
  router.back()
}
</script> 