import { useAuthStore } from '@/stores/auth'

export function useInitializeStore() {
  const authStore = useAuthStore()

  // Initialize any store-specific data here
  function initialize() {
    // Add any initialization logic
  }

  return {
    initialize
  }
} 