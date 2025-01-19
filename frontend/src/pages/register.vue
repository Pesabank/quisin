<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Create your account
        </h2>
      </div>
      <RegisterForm />
      <div class="text-center">
        <p class="text-sm text-gray-600">
          Already have an account?
          <NuxtLink 
            to="/login" 
            class="font-medium text-primary-600 hover:text-primary-500"
          >
            Sign in here
          </NuxtLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import RegisterForm from '@/components/auth/RegisterForm.vue'

const router = useRouter()
const authStore = useAuthStore()

// Redirect if already logged in
if (authStore.isAuthenticated) {
  const role = authStore.userRole
  const redirectPath = role ? `/${role.toLowerCase()}/dashboard` : '/dashboard'
  router.push(redirectPath)
}
</script> 