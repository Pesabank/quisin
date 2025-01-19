<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold text-gray-900">Customer Dashboard</h1>
      <div class="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
        <!-- Quick Actions -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-6">
            <h2 class="text-lg font-medium text-gray-900">Quick Actions</h2>
            <div class="mt-6 grid grid-cols-2 gap-4">
              <button class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000]">
                View Menu
              </button>
              <button class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000]">
                Make Reservation
              </button>
              <button class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000]">
                Order History
              </button>
              <button class="inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000]">
                My Profile
              </button>
            </div>
          </div>
        </div>

        <!-- Current Orders -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-6">
            <h2 class="text-lg font-medium text-gray-900">Current Orders</h2>
            <div class="mt-6">
              <p class="text-gray-500">No active orders</p>
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