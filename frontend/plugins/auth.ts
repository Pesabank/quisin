import { useAuthStore } from '~/stores/auth'

export default defineNuxtPlugin(async (nuxtApp) => {
  // Wait for Supabase plugin to be initialized
  await nuxtApp.runWithContext(async () => {
    const authStore = useAuthStore()
    
    try {
      // Initialize auth state
      await authStore.initializeAuth()
      console.log('Auth Plugin: Initialized successfully')
    } catch (error) {
      console.error('Auth Plugin: Initialization failed:', error)
    }
  })
}) 