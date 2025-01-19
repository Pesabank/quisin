<template>
  <div class="min-h-screen bg-gray-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 text-center">
        <h2 class="text-6xl font-bold text-gray-900 mb-4">404</h2>
        <p class="text-xl text-gray-600 mb-8">Page not found</p>
        <p class="text-gray-500 mb-8">The page you're looking for doesn't exist or has been moved.</p>
        <router-link
          to="/"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          Back to Home
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// No setup needed for this component
</script> 