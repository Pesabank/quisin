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
        <!-- Error Alert -->
        <div v-if="error" class="mb-4 p-4 rounded-md bg-red-50 border border-red-200">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <h3 class="text-sm font-medium text-red-800">{{ error }}</h3>
            </div>
          </div>
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
                class="appearance-none block w-full px-3 py-2 border border-gray-700 rounded-md shadow-sm placeholder-gray-500 bg-gray-800 text-white focus:outline-none focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
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
                class="appearance-none block w-full px-3 py-2 border border-gray-700 rounded-md shadow-sm placeholder-gray-500 bg-gray-800 text-white focus:outline-none focus:ring-[#FF6B00] focus:border-[#FF6B00] sm:text-sm"
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
                class="h-4 w-4 text-[#FF6B00] focus:ring-[#FF6B00] border-gray-700 rounded bg-gray-800"
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
              :disabled="isLoading"
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

const isLoading = ref(false)
const error = ref<string | null>(null)
const authStore = useAuthStore()

const form = ref({
  email: '',
  password: '',
  rememberMe: false
})

async function handleSubmit() {
  error.value = null
  isLoading.value = true
  
  try {
    console.log('Submitting form with:', { email: form.value.email })
    const response = await authStore.login({
      email: form.value.email,
      password: form.value.password
    })
    
    console.log('Login successful, user role:', authStore.userRole)

    // Role-based redirection
    if (authStore.isSuperAdmin) {
      console.log('Redirecting to superadmin dashboard')
      await navigateTo('/superadmin/dashboard')
    } else if (authStore.isAdmin) {
      console.log('Redirecting to admin dashboard')
      await navigateTo('/admin/dashboard')
    } else if (authStore.isKitchenStaff) {
      console.log('Redirecting to kitchen dashboard')
      await navigateTo('/kitchen/dashboard')
    } else if (authStore.isWaiter) {
      console.log('Redirecting to waiter dashboard')
      await navigateTo('/waiter/dashboard')
    } else {
      console.log('No specific role found, redirecting to home')
      await navigateTo('/')
    }
  } catch (err) {
    console.error('Login error:', err)
    error.value = err instanceof Error ? err.message : 'Failed to sign in. Please try again.'
  } finally {
    isLoading.value = false
  }
}
</script> 