<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold text-gray-900">Kitchen Dashboard</h1>
      <div class="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
        <!-- Active Orders -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-6">
            <h2 class="text-lg font-medium text-gray-900">Active Orders</h2>
            <div class="mt-6">
              <p class="text-gray-500">No active orders</p>
            </div>
          </div>
        </div>

        <!-- Order Queue -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-6">
            <h2 class="text-lg font-medium text-gray-900">Order Queue</h2>
            <div class="mt-6">
              <p class="text-gray-500">No orders in queue</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Logout Button -->
      <div class="mt-8">
        <button 
          @click="handleLogout"
          class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700"
        >
          Logout
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const handleLogout = () => {
  localStorage.removeItem('user')
  router.push('/sign-in')
}
</script> 