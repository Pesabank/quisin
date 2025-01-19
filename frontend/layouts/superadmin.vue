<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Navigation -->
    <nav class="bg-[#14161F] border-b border-gray-800">
      <div class="mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <!-- Logo -->
            <div class="flex-shrink-0 flex items-center">
              <div class="w-8 h-8 bg-[#FF6B00] rounded-lg flex items-center justify-center mr-2">
                <span class="text-xl font-bold text-white">Q</span>
              </div>
              <span class="text-xl font-bold text-white">Quisin</span>
            </div>
          </div>

          <!-- Right side navigation items -->
          <div class="flex items-center">
            <button
              class="p-2 rounded-md text-gray-400 hover:text-white focus:outline-none"
              @click="logout"
            >
              <span class="sr-only">Logout</span>
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </nav>

    <div class="flex">
      <!-- Sidebar -->
      <div class="w-64 bg-[#1A1C25] min-h-screen">
        <nav class="mt-5 px-2">
          <NuxtLink
            to="/superadmin/dashboard"
            class="group flex items-center px-2 py-2 text-base font-medium rounded-md text-gray-300 hover:bg-gray-700 hover:text-white"
            :class="{ 'bg-gray-700 text-white': $route.path === '/superadmin/dashboard' }"
          >
            <svg class="mr-4 h-6 w-6 text-gray-400 group-hover:text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            Dashboard
          </NuxtLink>

          <NuxtLink
            to="/superadmin/restaurants"
            class="mt-1 group flex items-center px-2 py-2 text-base font-medium rounded-md text-gray-300 hover:bg-gray-700 hover:text-white"
            :class="{ 'bg-gray-700 text-white': $route.path.startsWith('/superadmin/restaurants') }"
          >
            <svg class="mr-4 h-6 w-6 text-gray-400 group-hover:text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
            Restaurants
          </NuxtLink>

          <NuxtLink
            to="/superadmin/users"
            class="mt-1 group flex items-center px-2 py-2 text-base font-medium rounded-md text-gray-300 hover:bg-gray-700 hover:text-white"
            :class="{ 'bg-gray-700 text-white': $route.path.startsWith('/superadmin/users') }"
          >
            <svg class="mr-4 h-6 w-6 text-gray-400 group-hover:text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
            Users
          </NuxtLink>
        </nav>
      </div>

      <!-- Main content -->
      <div class="flex-1 p-8">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

function logout() {
  authStore.logout()
}
</script> 