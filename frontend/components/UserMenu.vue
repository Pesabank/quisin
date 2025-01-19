<template>
  <div>
    <button
      @click="isOpen = !isOpen"
      class="flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
    >
      <span class="sr-only">Open user menu</span>
      <div class="h-8 w-8 rounded-full bg-gray-300 flex items-center justify-center">
        {{ userInitials }}
      </div>
    </button>

    <div
      v-if="isOpen"
      class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-100"
    >
      <div class="py-1">
        <p class="px-4 py-2 text-sm text-gray-700">
          {{ user?.firstName }} {{ user?.lastName }}
        </p>
      </div>
      <div class="py-1">
        <NuxtLink
          :to="dashboardPath"
          class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
        >
          Dashboard
        </NuxtLink>
        <a
          href="#"
          class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          @click.prevent="handleLogout"
        >
          Sign out
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthState } from '~/composables/useAuthState'
import { useAuthStore } from '~/stores/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()
const { user, dashboardPath } = useAuthState()

const isOpen = ref(false)

const userInitials = computed(() => {
  if (!user.value) return ''
  return `${user.value.firstName[0]}${user.value.lastName[0]}`
})

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script> 