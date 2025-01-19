<template>
  <div>
    <h1 class="text-3xl font-bold text-white mb-8">Super Admin Dashboard</h1>
    
    <!-- Quick Actions -->
    <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4 mb-8">
      <!-- Add Restaurant -->
      <div class="bg-[#1A1C25] rounded-lg p-6 border border-gray-800">
        <div class="flex items-center">
          <div class="flex-shrink-0 bg-[#FF6B00] rounded-md p-3">
            <svg class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <div class="ml-4">
            <h2 class="text-lg font-semibold text-white">Add Restaurant</h2>
            <p class="text-gray-400">Register a new restaurant</p>
          </div>
        </div>
        <NuxtLink
          to="/superadmin/restaurants/new"
          class="mt-4 w-full inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#ff7b1a] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
        >
          Add New
        </NuxtLink>
      </div>

      <!-- Manage Users -->
      <div class="bg-[#1A1C25] rounded-lg p-6 border border-gray-800">
        <div class="flex items-center">
          <div class="flex-shrink-0 bg-[#FF6B00] rounded-md p-3">
            <svg class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <div class="ml-4">
            <h2 class="text-lg font-semibold text-white">Manage Users</h2>
            <p class="text-gray-400">View and manage users</p>
          </div>
        </div>
        <NuxtLink
          to="/superadmin/users"
          class="mt-4 w-full inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#ff7b1a] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00]"
        >
          View Users
        </NuxtLink>
      </div>
    </div>

    <!-- Statistics -->
    <div class="bg-[#1A1C25] rounded-lg p-6 border border-gray-800">
      <h2 class="text-xl font-semibold text-white mb-6">Overview</h2>
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <!-- Total Restaurants -->
        <div class="bg-[#14161F] rounded-lg p-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-400">Total Restaurants</p>
              <p class="mt-1 text-2xl font-semibold text-white">25</p>
            </div>
            <div class="bg-green-100 rounded-full p-2">
              <svg class="h-5 w-5 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
              </svg>
            </div>
          </div>
          <div class="mt-4">
            <div class="flex items-center">
              <span class="text-sm font-medium text-green-600">+12%</span>
              <span class="ml-2 text-sm text-gray-400">from last month</span>
            </div>
          </div>
        </div>

        <!-- Active Users -->
        <div class="bg-[#14161F] rounded-lg p-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-400">Active Users</p>
              <p class="mt-1 text-2xl font-semibold text-white">142</p>
            </div>
            <div class="bg-blue-100 rounded-full p-2">
              <svg class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
          </div>
          <div class="mt-4">
            <div class="flex items-center">
              <span class="text-sm font-medium text-blue-600">+8%</span>
              <span class="ml-2 text-sm text-gray-400">from last month</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'superadmin',
  middleware: ['auth'],
  meta: {
    auth: true,
    roles: ['SUPER_ADMIN']
  }
})
</script>
