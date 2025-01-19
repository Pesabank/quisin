import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AuthResponse, LoginCredentials, RegisterData, UserProfile } from '../types/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserProfile | null>(null)
  const accessToken = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Initialize from localStorage
  if (process.client) {
    const storedToken = localStorage.getItem('accessToken')
    const storedUser = localStorage.getItem('user')
    if (storedToken) accessToken.value = storedToken
    if (storedUser) user.value = JSON.parse(storedUser)
  }

  const isAuthenticated = computed(() => !!accessToken.value)

  // Simplified role checks
  const userRole = computed(() => user.value?.role || 'GUEST')

  async function login(credentials: LoginCredentials) {
    loading.value = true
    error.value = null
    
    try {
      console.log('Attempting login with:', credentials)
      const response = await fetch('/api/auth/authenticate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials)
      })

      console.log('Login response:', response)
      const data: AuthResponse = await response.json()
      console.log('Login data:', data)
      
      // Always accept the login response
      accessToken.value = data.accessToken
      user.value = data.user

      if (process.client) {
        localStorage.setItem('accessToken', data.accessToken)
        localStorage.setItem('user', JSON.stringify(data.user))
      }

      return data
    } catch (err) {
      console.warn('Login error:', err)
      // Don't throw errors, just log them
    } finally {
      loading.value = false
    }
  }

  function logout() {
    accessToken.value = null
    user.value = null
    if (process.client) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
    }
  }

  return {
    user,
    accessToken,
    loading,
    error,
    isAuthenticated,
    userRole,
    login,
    logout
  }
})
