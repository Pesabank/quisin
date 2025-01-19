<template>
  <div class="min-h-screen bg-[#14161F] flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <!-- Logo -->
      <div class="flex items-center justify-center mb-8">
        <div class="w-12 h-12 bg-[#FF6B00] rounded-lg flex items-center justify-center mr-3">
          <span class="text-2xl font-bold text-white">Q</span>
        </div>
        <span class="text-2xl font-bold text-white">Quisin</span>
      </div>
      <h2 class="text-center text-3xl font-extrabold text-white">Sign in to your account</h2>
      <p class="mt-2 text-center text-sm text-gray-400">
        Manage your restaurant with ease
      </p>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-[#1A1C25] py-8 px-4 shadow-2xl rounded-lg sm:px-10 border border-gray-800">
        <!-- Service Status -->
        <div v-if="!authStore.isReady" class="mb-4 p-4 rounded-md bg-red-900/50 border border-red-500">
          <p class="text-sm text-red-400">Authentication service not ready</p>
        </div>

        <!-- Error Alert -->
        <div v-if="error" class="mb-4 p-4 rounded-md bg-red-900/50 border border-red-500">
          <p class="text-sm text-red-400">{{ error }}</p>
        </div>

        <form class="space-y-6" @submit.prevent="handleSubmit">
          <div>
            <label for="email" class="block text-sm font-medium text-gray-200">
              Email address
            </label>
            <div class="mt-1">
              <input
                id="email"
                name="email"
                type="email"
                autocomplete="email"
                required
                v-model="form.email"
                :disabled="isLoading || !authStore.isReady"
                class="appearance-none block w-full px-3 py-2 border border-gray-700 rounded-md shadow-sm placeholder-gray-500 bg-gray-800 text-white focus:outline-none focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm disabled:opacity-50"
                placeholder="Enter your email"
              />
            </div>
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-200">
              Password
            </label>
            <div class="mt-1">
              <input
                id="password"
                name="password"
                type="password"
                autocomplete="current-password"
                required
                v-model="form.password"
                :disabled="isLoading || !authStore.isReady"
                class="appearance-none block w-full px-3 py-2 border border-gray-700 rounded-md shadow-sm placeholder-gray-500 bg-gray-800 text-white focus:outline-none focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm disabled:opacity-50"
                placeholder="Enter your password"
              />
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                id="remember-me"
                name="remember-me"
                type="checkbox"
                v-model="form.rememberMe"
                :disabled="isLoading || !authStore.isReady"
                class="h-4 w-4 text-[#FF6B00] focus:ring-[#FF6B00] border-gray-700 rounded bg-gray-800 disabled:opacity-50"
              />
              <label for="remember-me" class="ml-2 block text-sm text-gray-200">
                Remember me
              </label>
            </div>

            <div class="text-sm">
              <a href="#" class="font-medium text-[#FF6B00] hover:text-[#ff7b1a] transition-colors">
                Forgot your password?
              </a>
            </div>
          </div>

          <div>
            <button
              type="submit"
              class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#FF6B00] hover:bg-[#ff7b1a] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="isLoading || !authStore.isReady"
            >
              <template v-if="isLoading">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Signing in...
              </template>
              <template v-else>
                Sign in
              </template>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const isLoading = ref(false)
const error = ref('')

const form = ref({
  email: '',
  password: '',
  rememberMe: false
})

async function handleSubmit() {
  if (!authStore.isReady) {
    error.value = 'Authentication service not ready'
    return
  }

  error.value = ''
  isLoading.value = true
  
  try {
    await authStore.login({
      email: form.value.email,
      password: form.value.password
    })

    // Role-based redirection
    if (authStore.isSuperAdmin) {
      await navigateTo('/superadmin/dashboard')
    } else if (authStore.isAdmin) {
      await navigateTo('/admin/dashboard')
    } else if (authStore.isKitchenStaff) {
      await navigateTo('/kitchen/dashboard')
    } else if (authStore.isWaiter) {
      await navigateTo('/waiter/dashboard')
    } else if (authStore.isCustomer) {
      await navigateTo('/customer/dashboard')
    } else {
      await navigateTo('/')
    }
  } catch (e: any) {
    error.value = e.message || 'Failed to sign in. Please check your credentials.'
    console.error('Login error:', e)
  } finally {
    isLoading.value = false
  }
}
</script> 