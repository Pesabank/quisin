<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <div class="grid grid-cols-2 gap-4">
      <div>
        <label for="firstName" class="block text-sm font-medium text-gray-700">
          First Name
        </label>
        <input
          id="firstName"
          v-model="formData.firstName"
          type="text"
          required
          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
        />
      </div>
      <div>
        <label for="lastName" class="block text-sm font-medium text-gray-700">
          Last Name
        </label>
        <input
          id="lastName"
          v-model="formData.lastName"
          type="text"
          required
          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
        />
      </div>
    </div>

    <div>
      <label for="email" class="block text-sm font-medium text-gray-700">
        Email address
      </label>
      <input
        id="email"
        v-model="formData.email"
        type="email"
        required
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
      />
    </div>

    <div>
      <label for="phoneNumber" class="block text-sm font-medium text-gray-700">
        Phone Number
      </label>
      <input
        id="phoneNumber"
        v-model="formData.phoneNumber"
        type="tel"
        required
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
      />
    </div>

    <div>
      <label for="password" class="block text-sm font-medium text-gray-700">
        Password
      </label>
      <input
        id="password"
        v-model="formData.password"
        type="password"
        required
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
      />
    </div>

    <div>
      <label for="role" class="block text-sm font-medium text-gray-700">
        Role
      </label>
      <select
        id="role"
        v-model="formData.role"
        required
        class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500"
      >
        <option value="">Select a role</option>
        <option value="RESTAURANT_ADMIN">Restaurant Admin</option>
        <option value="WAITER">Waiter</option>
        <option value="KITCHEN_STAFF">Kitchen Staff</option>
        <option value="CUSTOMER">Customer</option>
      </select>
    </div>

    <div v-if="error" class="text-red-600 text-sm">
      {{ error }}
    </div>

    <button
      type="submit"
      :disabled="loading"
      class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
    >
      <span v-if="!loading">Register</span>
      <span v-else>Loading...</span>
    </button>
  </form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { RegisterData } from '@/types/auth'

const router = useRouter()
const authStore = useAuthStore()

const formData = reactive<RegisterData>({
  firstName: '',
  lastName: '',
  email: '',
  phoneNumber: '',
  password: '',
  role: undefined
})

const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  loading.value = true
  error.value = ''

  try {
    await authStore.register(formData)
    
    // Redirect based on role
    const redirectPath = formData.role ? `/${formData.role.toLowerCase()}/dashboard` : '/dashboard'
    router.push(redirectPath)
  } catch (err) {
    error.value = 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script> 