<template>
  <form @submit.prevent="handleSubmit" class="mt-8 space-y-6">
    <div class="rounded-md shadow-sm -space-y-px">
      <div>
        <label for="email" class="sr-only">Email address</label>
        <input
          id="email"
          v-model="formData.email"
          name="email"
          type="email"
          required
          class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-[#FF6B00] focus:border-[#FF6B00] focus:z-10 sm:text-sm"
          placeholder="Email address"
        />
      </div>
      <div>
        <label for="password" class="sr-only">Password</label>
        <input
          id="password"
          v-model="formData.password"
          name="password"
          type="password"
          required
          class="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-[#FF6B00] focus:border-[#FF6B00] focus:z-10 sm:text-sm"
          placeholder="Password"
        />
      </div>
    </div>

    <div v-if="authStore.error" class="text-red-600 text-sm text-center">
      {{ authStore.error }}
    </div>

    <div>
      <button
        type="submit"
        :disabled="authStore.loading"
        class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-[#FF6B00] hover:bg-[#e66000] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FF6B00] disabled:opacity-50"
      >
        <span v-if="!authStore.loading">Sign in</span>
        <span v-else>Signing in...</span>
      </button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import type { LoginCredentials } from '@/types/auth'

const emit = defineEmits<{
  (e: 'success'): void
}>()

const authStore = useAuthStore()

const formData = reactive<LoginCredentials>({
  email: '',
  password: ''
})

async function handleSubmit() {
  try {
    await authStore.login(formData)
    emit('success')
  } catch (err) {
    // Error is handled in the store
  }
}
</script> 